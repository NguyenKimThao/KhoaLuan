'use strict';

var properties = require('./implementation/DatabaseProperties'),
  elementHelper = require('../../../helper/ElementHelper'),
  cmdHelper = require('../../../helper/CmdHelper');
var is = require('bpmn-js/lib/util/ModelUtil').is;
var entryFactory = require('../../../factory/EntryFactory');
var $ = require('jquery');
var find = require('lodash/collection/find');


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
  return find(extensionElements.values, function (elem) {
    return is(elem, 'kltn:DataCondition') || is(elem, 'kltn:DatabaseInformation');
  });
}

function getPropertyElement(propElements) {
  return find(propElements.values, function (elem) {
    return is(elem, 'kltn:Field');
  });
}

module.exports = function (group, element, bpmnFactory, translate) {

  if ((is(element, "bpmn:DataOutputAssociation") || is(element, "bpmn:DataInputAssociation"))) {
    if (is(element.target, "bpmn:DataStoreReference") || is(element.source, "bpmn:DataStoreReference")) {
      var getValue = function (businessObject) {
        return function (element) {
          var cmd = [];
          var parent = businessObject.extensionElements;
          if (!parent) {
            return {};
          }
          var properties = getPropertiesElement(parent);
          if (!properties) {
            return {};
          }

          return properties.$attrs;
        };
      };

      var setValue = function (businessObject) {
        return function (element, values) {
          var cmd = [];
          var parent = businessObject.extensionElements;
          if (!parent) {
            parent = elementHelper.createElement('bpmn:ExtensionElements', { values: [] }, businessObject, bpmnFactory);
            cmd.push(cmdHelper.updateBusinessObject(element, businessObject, { extensionElements: parent }));
          }
          var properties = getPropertiesElement(parent);
          if (!properties) {
            properties = elementHelper.createElement('kltn:DataCondition', {}, parent, bpmnFactory);
            cmd.push(cmdHelper.addAndRemoveElementsFromList(
              element,
              parent,
              'values',
              'extensionElements',
              [properties],
              []
            ));
          }
          if (!properties.$attrs.action && is(element, "bpmn:DataOutputAssociation")) {
            properties.$attrs.action = 'insert';
          }
          cmd.push(cmdHelper.updateBusinessObject(element, properties, values));
          return cmd;
        };
      };

      var ipcRender = window.require('electron').ipcRenderer;
      var getBusinessObject = require('bpmn-js/lib/util/ModelUtil').getBusinessObject;

      var attr = {};
      var databaseattrs = {};
      if (getBusinessObject(element).extensionElements) {
        attr = getPropertiesElement(getBusinessObject(element).extensionElements).$attrs;
      }

      if (is(element, "bpmn:DataOutputAssociation")) {
        if (getBusinessObject(element.target).extensionElements) {
          databaseattrs = getPropertiesElement(getBusinessObject(element.target).extensionElements).$attrs;
        }
      } else {
        if (getBusinessObject(element.source).extensionElements) {
          databaseattrs = getPropertiesElement(getBusinessObject(element.source).extensionElements).$attrs;
        }
      }
      if (!databaseattrs) {
        alert('Bạn phải chọn database');
        return;
      }




      if (is(element, "bpmn:DataOutputAssociation")) {
        if (!attr.action) {
          attr.action = 'insert';
        }
        var action = entryFactory.comboBoxField({
          id: 'action',
          description: 'Choose Action With Your Database',
          label: 'Action',
          modelProperty: 'action',
          selectOptions: [
            { name: 'insert', value: 'Insert' },
            { name: 'update', value: 'Update' },
            { name: 'delete', value: 'Delete' },
            { name: 'procedure', value: 'Procedure' },
            { name: 'query', value: 'Query' }
          ],
          attributes: attr.action
        });
        action.get = getValue(getBusinessObject(element));
        action.set = setValue(getBusinessObject(element));
        group.entries.push(action);


      }
      if (is(element, "bpmn:DataInputAssociation")) {
        if (!attr.action) {
          attr.action = 'select';
        }
        var action = entryFactory.comboBoxField({
          id: 'action',
          description: 'Choose Action With Your Database',
          label: 'Action',
          modelProperty: 'action',
          selectOptions: [
            { name: 'select', value: 'Select' },
            { name: 'procedure', value: 'Procedure' },
            { name: 'query', value: 'Query' }
          ],
          attributes: attr.action
        });
        action.get = getValue(getBusinessObject(element));
        action.set = setValue(getBusinessObject(element));
        group.entries.push(action);

      }

      var fieldsOutput = [];
      if (attr.action && !(attr.action == 'delete' || attr.action == 'update' || attr.action == 'insert')) {
        if (attr.action == 'query' || attr.action == 'procedure') {
          if (!attr.typeOutput) {
            attr.typeOutput = '0';
          }

          fieldsOutput = [
            { name: '0', value: 'No Result' },
            { name: '1', value: 'Single Row' },
            { name: '2', value: 'Multil Row' },
            { name: '3', value: 'Select' }
          ]
        }
        else {
          if (!attr.typeOutput) {
            attr.typeOutput = '2';
          }
          fieldsOutput = [
            { name: '1', value: 'Single Row' },
            { name: '2', value: 'Multil Row' },
            { name: '3', value: 'Select' },
          ]
        }
      }


      if (attr.action && !(attr.action == 'query' || attr.action == 'procedure')) {

        var fieldsTable = [];
        if (databaseattrs) {
          var result = ipcRender.sendSync('client:GetTableName', databaseattrs);
          if (!result.err) {
            for (var i = 0; i < result.data.length; i++) {
              fieldsTable.push({ name: result.data[i], value: result.data[i] });
            }
          }
        }

        group.entries.push(entryFactory.comboBoxField({
          id: 'dataTable',
          description: 'Choose Table',
          label: 'Table',
          modelProperty: 'databaseTable',
          selectOptions: fieldsTable,
          get: getValue(getBusinessObject(element)),
          set: setValue(getBusinessObject(element)),
          attributes: attr.databaseTable
        }));

        var fields = [];
        if (attr.databaseTable) {
          var result = ipcRender.sendSync('client:GetColumnName', databaseattrs, attr.databaseTable);
          for (var i = 0; i < result.data.length; i++) {
            fields.push(result.data[i]);
          }
        }
        if (attr.action != 'delete') {
          group.entries.push(properties(element, bpmnFactory, {
            id: 'properties1',
            addLabel: translate('Add Column Result'),
            modelProperties: ['column', 'variable'],
            labels: [translate('Column'), translate('Variable')],
            fields: fields,
            getParent: function (element, node, bo) {
              return bo.extensionElements;
            },

            createParent: function (element, bo) {
              var parent = elementHelper.createElement('bpmn:ExtensionElements', { values: [] }, bo, bpmnFactory);
              var cmd = cmdHelper.updateBusinessObject(element, bo, { extensionElements: parent });
              return {
                cmd: cmd,
                parent: parent
              };
            }
          }, translate));
        }
      }

      if (attr.action && attr.action == 'procedure') {
        var fieldProcudeName = [];
        var result = ipcRender.sendSync('client:GetProcudeName', databaseattrs);
        if (!result.err) {
          for (var i = 0; i < result.data.length; i++) {
            fieldProcudeName.push({ name: result.data[i], value: result.data[i] });
          }
        }
        group.entries.push(entryFactory.comboBoxField({
          id: 'nameprocedure',
          description: 'Choose Name Procedure',
          label: 'Name Procedure',
          modelProperty: 'nameprocedure',
          selectOptions: fieldProcudeName,
          attributes: attr.nameprocedure,
          get: getValue(getBusinessObject(element)),
          set: setValue(getBusinessObject(element)),
        }));

        var fieldsParameter = [];
        if (attr.nameprocedure) {
          var result = ipcRender.sendSync('client:GetParameterProcedure', databaseattrs, attr.nameprocedure);
          for (var i = 0; i < result.data.length; i++) {
            fieldsParameter.push({ name: result.data[i].name, value: result.data[i] });
          }
        }
        group.entries.push(properties(element, bpmnFactory, {
          id: 'properties2',
          addLabel: translate('Add Parameter'),
          modelProperties: ['column', 'variable'],
          labels: [translate('Parameter'), translate('Variable')],
          fields: fieldsParameter,
          getParent: function (element, node, bo) {
            return bo.extensionElements;
          },

          createParent: function (element, bo) {
            var parent = elementHelper.createElement('bpmn:ExtensionElements', { values: [] }, bo, bpmnFactory);
            var cmd = cmdHelper.updateBusinessObject(element, bo, { extensionElements: parent });
            return {
              cmd: cmd,
              parent: parent
            };
          }
        }, translate));
      }
      if (attr.action && attr.action == 'query') {
        group.entries.push(entryFactory.textBox({
          id: 'execquery',
          label: 'Write Query',
          modelProperty: 'execquery',
          get: getValue(getBusinessObject(element)),
          set: setValue(getBusinessObject(element))
        }));
      }


      if ((attr.typeOutput && attr.typeOutput != '0')) {
        if(attr.action != 'select'|| (attr.action == 'select' && attr.typeOutput != '1')) {
          //Thêm Name Variables
          group.entries.push(entryFactory.textField({
            id: 'nameVariables',
            label: 'Name Variables',
            modelProperty: 'nameVariables',
            get: getValue(getBusinessObject(element)),
            set: setValue(getBusinessObject(element)),
            hidden: function (element, node) {
              if (attr.action == 'update' || attr.action == 'delete' || attr.action == 'insert')
                return true;
              return false;
            }
          }));
        }
      }

      if (attr.action && !(attr.action == 'delete' || attr.action == 'update' || attr.action == 'insert')) {
        group.entries.push(entryFactory.comboBoxField({
          id: 'typeOutput',
          description: 'Choose Type Output',
          label: 'typeOutput',
          modelProperty: 'typeOutput',
          selectOptions: fieldsOutput,
          attributes: attr.typeOutput,
          get: getValue(getBusinessObject(element)),
          set: setValue(getBusinessObject(element)),
        }));
        if (attr.typeOutput == '3') {
          group.entries.push(entryFactory.textField({
            id: 'keymultirow',
            label: 'Key Multil Row ',
            modelProperty: 'keymultirow',
            get: getValue(getBusinessObject(element)),
            set: setValue(getBusinessObject(element))
          }));
          group.entries.push(entryFactory.textField({
            id: 'textmultirow',
            label: 'Text multi Row',
            modelProperty: 'textmultirow',
            get: getValue(getBusinessObject(element)),
            set: setValue(getBusinessObject(element))
          }));
        } else
          if (attr.typeOutput != '0' && attr.action != 'select') {
            var props = require('./implementation/ServiceProperties');
            group.entries.push(props(element, bpmnFactory, {
              id: 'propertiesOutput',
              addLabel: translate('Add Output'),
              modelProperties: ['column', 'variable'],
              labels: [translate('ParamOutput'), translate('variableOutput')],

              getParent: function (element, node, bo) {
                return bo.extensionElements;
              },

              createParent: function (element, bo) {
                var parent = elementHelper.createElement('bpmn:ExtensionElements', { values: [] }, bo, bpmnFactory);
                var cmd = cmdHelper.updateBusinessObject(element, bo, { extensionElements: parent });
                return {
                  cmd: cmd,
                  parent: parent
                };
              }
            }, translate));
          }

      }
      if (attr.action && !(attr.action == 'query' || attr.action == 'procedure')) {
        group.entries.push(entryFactory.textField({
          id: 'condition',
          description: 'Condition',
          label: 'Condition',
          modelProperty: 'condition',
          get: getValue(getBusinessObject(element)),
          set: setValue(getBusinessObject(element))
        }));
      }
      // }
      // else {
      //   // phần mới thêm vào để xử lý 'query' và 'procedure'
      //   {
      //     if (attr.typeAction != 'query') {
      //       var nameprocedure = entryFactory.comboBoxField({
      //         id: 'nameprocedure',
      //         description: 'Choose Name Procedure',
      //         label: 'Name Procedure',
      //         modelProperty: 'nameprocedure',
      //         selectOptions: [],
      //         onClick: function () {
      //           var result = ipcRender.sendSync('client:GetProcudeName', databaseattrs);
      //           var valCurrent = $("#camunda-nameprocedure").val();
      //           $("#camunda-nameprocedure").empty();
      //           if (result.err) {
      //             alert('Error when get procedure name. Please enter correct information in datastoreReference!');
      //             return;
      //           }
      //           var isCurrent = false;
      //           for (var i = 0; i < result.data.length; i++) {
      //             if (valCurrent && valCurrent === result.data[i]) {
      //               $("#camunda-nameprocedure").append('<option value=' + result.data[i] + ' selected>' + result.data[i] + '</option>');
      //               attr.nameprocedure = result.data[i];
      //               isCurrent = true;
      //             }
      //             else
      //               $("#camunda-nameprocedure").append('<option value=' + result.data[i] + '>' + result.data[i] + '</option>');
      //           }
      //           if (!isCurrent)
      //             attr.nameprocedure = result.data[0];
      //         },
      //         attributes: attr.nameprocedure
      //       });
      //       group.entries.push(nameprocedure);


      //       if (attr.nameprocedure) {
      //         var props = require('./implementation/ServiceProperties');
      //         var propertiesEntryInput = props(element, bpmnFactory, {
      //           id: 'propertiesInput',
      //           modelProperties: ['paramInput', 'variableInput'],
      //           labels: [translate('paramInput'), translate('paramInput')],

      //           getParent: function (element, node, bo) {
      //             console.log('getParent');
      //             console.log(bo);
      //             return bo.extensionElements;
      //           },

      //           createParent: function (element, bo) {
      //             var parent = elementHelper.createElement('bpmn:ExtensionElements', { values: [] }, bo, bpmnFactory);
      //             var cmd = cmdHelper.updateBusinessObject(element, bo, { extensionElements: parent });
      //             return {
      //               cmd: cmd,
      //               parent: parent
      //             };
      //           }
      //         }, translate);

      //         if (propertiesEntryInput) {
      //           group.entries.push(propertiesEntryInput);
      //         }
      //       }
      //     }
      //     else {
      //       var execquery = entryFactory.textBox({
      //         id: 'execquery',
      //         label: 'Write Query',
      //         modelProperty: 'execquery'
      //       });
      //       execquery.get = getValue(getBusinessObject(element));
      //       execquery.set = setValue(getBusinessObject(element));
      //       group.entries.push(execquery);
      //     }
      //     if (!attr.typeOutput) {
      //       attr.typeOutput = '0';
      //     }
      //     var typeOutput = entryFactory.comboBoxField({
      //       id: 'typeOutput',
      //       description: 'Choose Type Output',
      //       label: 'typeOutput',
      //       modelProperty: 'typeOutput',
      //       selectOptions: [
      //         { name: '0', value: 'No Result' },
      //         { name: '1', value: 'Single Row' },
      //         { name: '2', value: 'Multil Row' }
      //       ],
      //       attributes: attr.typeOutput
      //     });
      //     typeOutput.get = getValue(getBusinessObject(element));
      //     typeOutput.set = setValue(getBusinessObject(element));
      //     group.entries.push(typeOutput);


    }
    else {
      var props = require('./implementation/ServiceProperties');
      var propertiesEntry = props(element, bpmnFactory, {
        id: 'properties1',
        modelProperties: ['column', 'variable'],
        labels: [translate('Column'), translate('Varable')],

        getParent: function (element, node, bo) {
          return bo.extensionElements;
        },

        createParent: function (element, bo) {
          var parent = elementHelper.createElement('bpmn:ExtensionElements', { values: [] }, bo, bpmnFactory);
          var cmd = cmdHelper.updateBusinessObject(element, bo, { extensionElements: parent });
          return {
            cmd: cmd,
            parent: parent
          };
        }
      }, translate);

      if (propertiesEntry) {
        group.entries.push(propertiesEntry);
      }
    }
  }
};
