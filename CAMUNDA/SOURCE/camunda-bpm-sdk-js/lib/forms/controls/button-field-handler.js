'use strict';

var constants = require('../constants'),
    AbstractFormField = require('./abstract-form-field');


/**
 * A field control handler for simple text / string values
 * @class
 * @memberof CamSDK.form
 * @augments {CamSDK.form.AbstractFormField}
 */
var ButtonFieldHandler = AbstractFormField.extend(
/** @lends CamSDK.form.ButtonFieldHandler.prototype */
  {
  /**
   * Prepares an instance
   */
    initialize: function() {
    // read variable definitions from markup
      var variableName = this.element.attr(constants.DIRECTIVE_CAM_VARIABLE_NAME);
      var variableType = this.element.attr(constants.DIRECTIVE_CAM_VARIABLE_TYPE);
    // crate variable
      this.variableManager.createVariable({
        name: variableName,
        type: variableType
      });

    // remember the original value found in the element for later checks
      this.originalValue = this.element.attr('value');

      this.previousValue = this.originalValue;

    // remember variable name
      this.variableName = variableName;

      this.getValue();
    },

  /**
   * Applies the stored value to a field element.
   *
   * @return {CamSDK.form.ButtonFieldHandler} Chainable method
   */
    applyValue: function() {

      return this;
    },

  /**
   * Retrieves the value from an <input>
   * element and stores it in the Variable Manager
   *
   * @return {*}
   */
    getValue: function() {
      var value = this.element.attr('value');
      return value;
    }
  },
/** @lends CamSDK.form.ButtonFieldHandler */
  {

    selector: 'button['+ constants.DIRECTIVE_CAM_VARIABLE_NAME +']'

  });

module.exports = ButtonFieldHandler;

