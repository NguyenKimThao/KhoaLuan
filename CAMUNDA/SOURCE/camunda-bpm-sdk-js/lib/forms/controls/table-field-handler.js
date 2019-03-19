'use strict';

var constants = require('../constants'),
    AbstractFormField = require('./abstract-form-field');


/**
 * A field control handler for choices
 * @class
 * @memberof CamSDK.form
 * @augments {CamSDK.form.AbstractFormField}
 */
var TableFieldHandler = AbstractFormField.extend(
/** @lends CamSDK.form.TableFieldHandler.prototype */
  {
  /**
   * Prepares an instance
   */
    initialize: function() {
    // read variable definitions from markup
      var choicesVariableName = this.choicesVariableName = this.element.attr(constants.DIRECTIVE_CAM_CHOICES);

    // fetch choices variable
      if(choicesVariableName) {
        this.variableManager.fetchVariable(choicesVariableName);
      }
      var varmanager=this.variableManager;
      var tr=this.element.find('tbody').find('tr');
      if(tr) {
        tr.find('td').each(function() {
          var p=this.getElementsByTagName('p');
          if(p&&p[0]&&p[0].getAttribute(constants.DIRECTIVE_CAM_CHOICES)) {
            varmanager.fetchVariable(this.getElementsByTagName('p')[0].getAttribute(constants.DIRECTIVE_CAM_CHOICES));
          }
        });
      }

    // remember the original value found in the element for later checks
      this.originalValue =  null;

      this.previousValue = this.originalValue;

    // remember variable name
      this.variableName = choicesVariableName;
    },

  /**
   * Applies the stored value to a field element.
   *
   * @return {CamSDK.form.TableFieldHandler} Chainable method.
   */

    applyValue: function() {
      if(this.choicesVariableName) {
        var choicesVariableValue = this.variableManager.variableValue(this.choicesVariableName);
        if(choicesVariableValue) {
          this.element.scope()[this.choicesVariableName]=choicesVariableValue;
          var tr= this.element.find('tbody').find('tr');
          tr.attr('ng-repeat','item in '+ this.choicesVariableName);
          tr.find('td').each(function() {
            var text=this.innerHTML;
            var p=this.getElementsByTagName('p');
            if(p&&p[0]&&p[0].getAttribute(constants.DIRECTIVE_CAM_CHOICES)) {
              text=text.replace(/\[/g, '[item.');
              text=text.replace(/{/g, '{{');
              text=text.replace(/}/g, '}}');
            } 
            else {
              text=text.replace(/{/g, '{{item.');
              text=text.replace(/}/g, '}}');
            }
            this.innerHTML=text;
          });
          var scope = this.element.scope();
          var element=this.element;
          var injector = element.injector();
          if (!injector) { return; }
          injector.invoke(['$compile', function($compile) {
            $compile(element)(scope);
          }]);
        }
      }
      return this;
    },

  /**
   * Retrieves the value from a field element and stores it
   *
   * @return {*} when multiple choices are possible an array of values, otherwise a single value
   */
    getValue: function() {
      var value=this.variableManager.variableValue(this.variableName);
      this.variableManager.variableValue(this.variableName, value);
      return value;
    }

  },
/** @lends CamSDK.form.TableFieldHandler */
  {
    selector: 'table['+ constants.DIRECTIVE_CAM_CHOICES +']'

  });

module.exports = TableFieldHandler;

