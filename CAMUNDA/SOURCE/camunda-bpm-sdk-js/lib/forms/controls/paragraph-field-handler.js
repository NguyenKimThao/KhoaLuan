'use strict';

var constants = require('../constants'),
    AbstractFormField = require('./abstract-form-field');


/**
 * A field control handler for choices
 * @class
 * @memberof CamSDK.form
 * @augments {CamSDK.form.AbstractFormField}
 */
var ParagraphFieldHandler = AbstractFormField.extend(
/** @lends CamSDK.form.SelectFieldHandler.prototype */
  {
  /**
   * Prepares an instance
   */
    initialize: function() {
    // read variable definitions from markup
      var variableName = this.variableName = this.element.attr(constants.DIRECTIVE_CAM_VARIABLE_NAME);
      var variableType = this.variableType = this.element.attr(constants.DIRECTIVE_CAM_VARIABLE_TYPE);
      var choicesVariableName = this.choicesVariableName = this.element.attr(constants.DIRECTIVE_CAM_CHOICES);

    // crate variable
      this.variableManager.createVariable({
        name: variableName,
        type: variableType,
        value: this.element.attr('value')|| null
      });

    // fetch choices variable
      if(choicesVariableName) {
        this.variableManager.fetchVariable(choicesVariableName);
      }

    // remember the original value found in the element for later checks
      this.originalValue = this.element.attr('value') || null;

      this.previousValue = this.originalValue;

    // remember variable name
      this.variableName = variableName;

      this.getValue();
    },

  /**
   * Applies the stored value to a field element.
   *
   * @return {CamSDK.form.SelectFieldHandler} Chainable method.
   */
    applyValue: function() {

      var variableValue = this.variableManager.variableValue(this.variableName);
      if(this.choicesVariableName) {
        var choicesVariableValue = this.variableManager.variableValue(this.choicesVariableName);
        if(choicesVariableValue) {
          var camValue=this.element.attr(constants.DIRECTIVE_CAM_VALUE);
          var camText= this.element.attr(constants.DIRECTIVE_CAM_TEXT);
          this.element.scope()[this.choicesVariableName]=this.choicesVariableValue;
        // array
          if (choicesVariableValue instanceof Array) {
            for(var i = 0; i < choicesVariableValue.length; i++) {
              var val = choicesVariableValue[i];
              if(val instanceof Object) {
                if(val[camValue] == variableValue) {
                  for (var key in val) {
                    camText = camText.replace(new RegExp('\\{' + key + '\\}', 'gi'),val[key]);
                  }
                  this.element.html(camText);
                  break;
                }
              }
              else
              {
                this.element.html(val);
              }
            }
        // object aka map
          } else {
            for (var p in choicesVariableValue) {
              if(p==variableValue)
                this.element.html(choicesVariableValue[p]);
            }
          }
        }
      }

      this.element.trigger('camFormVariableApplied', variableValue);

      return this;
    },

  /**
   * Retrieves the value from a field element and stores it
   *
   * @return {*} when multiple choices are possible an array of values, otherwise a single value
   */
    getValue: function() {
      var value=this.element.attr('value');
      this.variableManager.variableValue(this.variableName, value);
      return value;
    }

  },
/** @lends CamSDK.form.SelectFieldHandler */
  {
    selector: 'p['+ constants.DIRECTIVE_CAM_VARIABLE_NAME +']'

  });

module.exports = ParagraphFieldHandler;

