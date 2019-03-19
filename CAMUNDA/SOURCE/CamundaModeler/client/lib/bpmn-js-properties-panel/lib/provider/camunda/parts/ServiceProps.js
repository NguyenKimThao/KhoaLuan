'use strict';


var entryFactory = require('../../../factory/EntryFactory');
var isAny = require('bpmn-js/lib/features/modeling/util/ModelingUtil').isAny,
  domQuery = require('min-dom/lib/query'),
  cmdHelper = require('../../../helper/CmdHelper'),
  elementHelper = require('../../../helper/ElementHelper'),
  script = require('./implementation/Script')('language', 'body', true),
  $=require('jquery'),
  find = require('lodash/collection/find');
var domify = require('min-dom/lib/domify');
var is = require('bpmn-js/lib/util/ModelUtil').is;
var $ =require('jquery');
function getContainer(node) {
  return domQuery('div[data-list-entry-container]', node);
}
function getPropertyValues(parent) {
  var properties = parent && getPropertiesElement(parent);
  if (properties && properties.values) {
    return properties.values;
  }
  return [];
}
function getPropertiesElement(element) {
  if (!isExtensionElements(element)) {
    return element.properties;
  } else {
    return getPropertiesElementInsideExtensionElements(element);
  }
}

function isExtensionElements(element) {
  return is(element, 'bpmn:ExtensionElements');
}

function getPropertiesElementInsideExtensionElements(extensionElements) {
  return find(extensionElements.values, function(elem) {
    return is(elem, 'kltn:Service');
  });
}

function getTaskFormField(element) {
  if (!isExtensionElements(element)) {
    return element.properties;
  } else {
    return find(element.values, function(elem) {
      return is(elem, 'camunda:FormData');
    });
  }
}



module.exports = function(group, element, bpmnFactory, translate) {

  // Only return an entry, if the currently selected
  // element is a start event.
  var getValue = function(businessObject) {
    return function(element) {
      var cmd=[];
      var parent=businessObject.extensionElements;
      if(!parent) {
        return {};
      }
      var properties=getPropertiesElement(parent);
      if(!properties) {
        return {};
      }
      return properties.$attrs;
    };
  };

  var setValue = function(businessObject) {
    return function(element, values) {
      var cmd=[];
      var parent=businessObject.extensionElements;
      if(!parent) {
        parent = elementHelper.createElement('bpmn:ExtensionElements', {values: []}, businessObject, bpmnFactory);
        cmd.push(cmdHelper.updateBusinessObject(element, businessObject, {extensionElements: parent}));
      }
      var properties=getPropertiesElement(parent);
      if(!properties) {
        properties = elementHelper.createElement('kltn:Service', {}, parent, bpmnFactory);
        cmd.push(cmdHelper.addAndRemoveElementsFromList(
          element,
          parent,
          'values',
          'extensionElements',
          [properties],
          []
        ));
      }
      var val={};
      if(values[0]) {
        val=values[0];
      }else {
        val=values;
      }
      if(val.filename){
        val.fileid=val.filename;
        val.filename=filenames[val.filename];
        properties.sheetId=null;
      }
      cmd.push(cmdHelper.updateBusinessObject(element,properties,val));
      if(!properties.servicetype){
        properties.servicetype='googledrive';
      }
      if(!properties.token){
        properties.token=attr.token;
      }

      return cmd;
    };
  };
  var attr={};
  var filenames={};
  if (is(element, 'bpmn:DataObjectReference')) {
    var ipcRender=window.require('electron').ipcRenderer;
    var getBusinessObject = require('bpmn-js/lib/util/ModelUtil').getBusinessObject;
    if(getBusinessObject(element).extensionElements){
      attr=getPropertiesElement(getBusinessObject(element).extensionElements);
    }


    var serviceType=entryFactory.comboBoxField({
      id : 'serviceType',
      description : 'Choose Service',
      label : 'Service Type',
      modelProperty : 'service',
      selectOptions: [
        { name: 'googledrive', value: 'Google Drive' },
      ],
      attributes:attr.servicetype
    });
    serviceType.get=getValue(getBusinessObject(element));
    serviceType.set=setValue(getBusinessObject(element));
    group.entries.push(serviceType);

    var fileName = entryFactory.comboBoxField({
      id: 'fileName',
      description: 'Choose File Name',
      label: 'File Name',
      modelProperty: 'filename',
      selectOptions: [],
      attributes: attr.filename,
      onClick: function (e) {
        var result = ipcRender.sendSync('client:GetFileName', attr);

        if (result.err&&result.err!='windowclosed') {
          alert('Error when get file name. Please enter correct information!');
          return;
        }
        if(result.data) {
          $("#camunda-fileName").empty();
          if(result.data.files.length==0){
            alert('No file found');
          }
          for (var i = 0; i < result.data.files.length; i++) {
            $("#camunda-fileName").append('<option value=' + result.data.files[i].id + '>' + result.data.files[i].name + '</option>');
            filenames[result.data.files[i].id]=result.data.files[i].name
          }
          attr.fileid = result.data.files[0].id;
          attr.filename = result.data.files[0].name;
          if(result.data.token) {
            attr.token=JSON.stringify(result.data.token);
            $("#camunda-fileName").click();
          }
        }
      }


    });
    fileName.get = getValue(getBusinessObject(element));
    fileName.set = setValue(getBusinessObject(element));
    group.entries.push(fileName);

    var button=entryFactory.button({
      id : 'test',
      description : '',
      label : 'Apply',
      modelProperty : 'sheetId',
      onClick : function (element, node, event, scopeNode) {
        if(!attr.fileid){
			alert('Please choose file name');
			return;
		}
        if (!element.incoming.length) {
          alert('Please connect to UserTask');
          return;
        }
        if (!element.incoming[0].source) {
          alert('Please connect to UserTask');
          return;
        }
        var usertask = getBusinessObject(element.incoming[0].source);
        if (usertask.$type != 'bpmn:UserTask') {
          alert('Please connect to UserTask');
          return;
        }
		
		if(!usertask.extensionElements){
            alert("Please enter form field");
			return;
		}
        if (usertask.extensionElements && usertask.name) {
          var formFields = getTaskFormField(usertask.extensionElements).fields;
          if (!formFields) {
            alert("Please enter form field");
			return;
          }
          var ids = [];
          for (var i = 0; i < formFields.length; i++) {
            ids.push(formFields[i].id);
          }

          var result = ipcRender.sendSync('client:WriteHeader', attr, usertask.name, ids);
          if (result.err) {
            alert('error when apply your service, please try again');
          } else {
            $("input[name='sheetId']").val(result.data);
            alert('Apply Successfully');
            return true;
          }
        } else {
          if (usertask.name == null || usertask.name == "") {
            alert("Please enter UserTask name");
            return;
          }
        }
      }
    });
    button.set=setValue(getBusinessObject(element));
    group.entries.push(button);

  }
};
