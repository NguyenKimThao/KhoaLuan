'use strict';

var getBusinessObject = require('bpmn-js/lib/util/ModelUtil').getBusinessObject,
  is = require('bpmn-js/lib/util/ModelUtil').is;

var factory = require('../../../../factory/EntryFactory');

var elementHelper = require('../../../../helper/ElementHelper'),
  extensionElementsHelper = require('../../../../helper/ExtensionElementsHelper'),
  cmdHelper = require('../../../../helper/CmdHelper'),
  utils = require('../../../../Utils');

var assign = require('lodash/object/assign'),
  forEach = require('lodash/collection/forEach'),
  find = require('lodash/collection/find');

function generatePropertyId() {
  return utils.nextId('Property_');
}

/**
 * Get all camunda:property objects for a specific business object
 *
 * @param  {ModdleElement} parent
 *
 * @return {Array<ModdleElement>} a list of camunda:property objects
 */
function getPropertyValues(parent) {
  var properties = parent && getPropertiesElement(parent);
  if (properties && properties.values) {
    return properties.values;
  }
  return [];
}

/**
 * Get all camunda:Properties object for a specific business object
 *
 * @param  {ModdleElement} parent
 *
 * @return {ModdleElement} a camunda:Properties object
 */
function getPropertiesElement(element) {
  if (!isExtensionElements(element)) {
    return element.properties;
  } else {
    return getPropertiesElementInsideExtensionElements(element);
  }
}

/**
 * Get first camunda:Properties object for a specific bpmn:ExtensionElements
 * business object.
 *
 * @param {ModdleElement} extensionElements
 *
 * @return {ModdleElement} a camunda:Properties object
 */
function getPropertiesElementInsideExtensionElements(extensionElements) {
  return find(extensionElements.values, function (elem) {
    return is(elem, 'kltn:DataStoreFields');
  });
}

/**
 * Returns true, if the given business object is a bpmn:ExtensionElements.
 *
 * @param {ModdleElement} element
 *
 * @return {boolean} a boolean value
 */
function isExtensionElements(element) {
  return is(element, 'bpmn:ExtensionElements');
}

/**
 * Create a camunda:property entry using tableEntryFactory
 *
 * @param  {djs.model.Base} element
 * @param  {BpmnFactory} bpmnFactory
 * @param  {Object} options
 * @param  {string} options.id
 * @param  {Array<string>} options.modelProperties
 * @param  {Array<string>} options.labels
 * @param  {function} options.getParent Gets the parent business object
 * @param  {function} options.show Indicate when the entry will be shown, should return boolean
 */
module.exports = function (element, bpmnFactory, options, translate) {

  var getParent = options.getParent;

  var modelProperties = options.modelProperties,
    createParent = options.createParent;

  var bo = getBusinessObject(element);
  if (is(element, 'bpmn:Participant')) {
    bo = bo.get('processRef');
  }

  // build properties group only when the participant have a processRef
  if (!bo) {
    return;
  }
  assign(options, {
    addLabel: translate('Parameter'),
    getElements: function (element, node) {
      var parent = getParent(element, node, bo);
      return getPropertyValues(parent);
    },
    updateElement: function (element, value, node, idx) {
      var parent = getParent(element, node, bo),
        property = getPropertyValues(parent)[idx];
      forEach(modelProperties, function (prop) {
        value[prop] = value[prop] || undefined;
      });

      return cmdHelper.updateBusinessObject(element, property, value);
    },
    validate: function (element, value, node, idx) {
      // validate id if necessary
      if (modelProperties.indexOf('id') >= 0) {

        var parent = getParent(element, node, bo),
          properties = getPropertyValues(parent),
          property = properties[idx];

        if (property) {
          // check if id is valid
          var validationError = utils.isIdValid(property, value.id);

          if (validationError) {
            return { id: validationError };
          }
        }
      }
    },
  });

  return factory.customParamer(options);
};
