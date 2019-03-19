'use strict';


var entryFactory = require('../../../factory/EntryFactory');
var isAny = require('bpmn-js/lib/features/modeling/util/ModelingUtil').isAny,
  domQuery = require('min-dom/lib/query'),
  cmdHelper = require('../../../helper/CmdHelper'),
  elementHelper = require('../../../helper/ElementHelper'),
  script = require('./implementation/Script')('language', 'body', true),
  $=require('jquery'),
  find = require('lodash/collection/find');
var is = require('bpmn-js/lib/util/ModelUtil').is;

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
    return is(elem, 'kltn:DatabaseInformation');
  });
}

function getPropertyElement(propElements) {
  return find(propElements.values, function(elem) {
    return is(elem, 'camunda:Property');
  });
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
        properties = elementHelper.createElement('kltn:DatabaseInformation', {}, parent, bpmnFactory);
        cmd.push(cmdHelper.addAndRemoveElementsFromList(
          element,
          parent,
          'values',
          'extensionElements',
          [properties],
          []
        ));
      }
      cmd.push(cmdHelper.updateBusinessObject(element,properties,values));
      if(!properties.$attrs.databaseType){
        properties.$attrs.databaseType='mssql';
      }

      return cmd;
    };
  };

  if (is(element, 'bpmn:DataStoreReference')) {
    var ipcRender=window.require('electron').ipcRenderer;
    var getBusinessObject = require('bpmn-js/lib/util/ModelUtil').getBusinessObject;
    var attr={};
    if(getBusinessObject(element).extensionElements){
      attr=getPropertiesElement(getBusinessObject(element).extensionElements).$attrs;
    }



    var databaseType=entryFactory.comboBoxField({
      id : 'databaseType',
      description : 'Choose Database Type',
      label : 'Database Type',
      modelProperty : 'databaseType',
      selectOptions: [
        { name: 'mssql', value: 'SQL Server' },
        { name: 'mysql', value: 'MySQL' },
        { name: 'oracle', value: 'Oracle' }
      ],
      attributes:attr.databaseType
    });
    databaseType.get=getValue(getBusinessObject(element));
    databaseType.set=setValue(getBusinessObject(element));
    group.entries.push(databaseType);

    var connectionString=entryFactory.textField({
      id : 'cnns',
      description : 'Server Host',
      label : 'Connection String',
      modelProperty : 'server'
    });

    connectionString.get=getValue(getBusinessObject(element));
    connectionString.set=setValue(getBusinessObject(element));
    group.entries.push(connectionString);

    var username=entryFactory.textField({
      id : 'usn',
      description : 'Username to connect to server',
      label : 'User Name',
      modelProperty : 'username'
    });

    username.get=getValue(getBusinessObject(element));
    username.set=setValue(getBusinessObject(element));
    group.entries.push(username);

    var password= entryFactory.textField({
      id : 'pw',
      description : 'Password to connect to server',
      label : 'Password',
      modelProperty : 'password',
	  kind:'password'
    });

    password.get=getValue(getBusinessObject(element));
    password.set=setValue(getBusinessObject(element));
    group.entries.push(password);

    if(attr.databaseType && attr.databaseType=='oracle'){
      var db= entryFactory.textField({
        id : 'db',
        description : 'Type Database',
        label : 'Database',
        modelProperty : 'database'
      });

      db.get=getValue(getBusinessObject(element));
      db.set=setValue(getBusinessObject(element));
      group.entries.push(db);
    }else {
      var database = entryFactory.comboBoxField({
        id: 'database',
        description: 'Choose Database',
        label: 'Database',
        modelProperty: 'database',
        selectOptions: [],
        attributes: attr.database,
        onClick: function (e) {
          var attrs = getPropertiesElement(getBusinessObject(element).extensionElements).$attrs;
          var result = ipcRender.sendSync('client:GetDatabaseName', attrs);
          $("#camunda-database").empty();
		  attr.database="";
          if (result.err) {
            alert('Error when get database name. Please enter correct information!');
            return;
          }
          for (var i = 0; i < result.data.length; i++) {
            $("#camunda-database").append('<option value=' + result.data[i] + '>' + result.data[i] + '</option>');
          }
          attrs.database = result.data[0];
        }


      });
      database.get = getValue(getBusinessObject(element));
      database.set = setValue(getBusinessObject(element));
      group.entries.push(database);
    }
    group.entries.push(entryFactory.button({
      id : 'test',
      description : 'Choose Database Type',
      label : 'Test Connection',
      modelProperty : 'password1',
      onClick : function (e) {
        var prop=getPropertiesElement(getBusinessObject(element).extensionElements).$attrs;
        var result=ipcRender.sendSync('client:ConnectDatabase',prop);

        if(result){
          alert('Connect Fail');
        }else {
          alert('Connect Successfully');
        }
      }
    }));
    }
};
