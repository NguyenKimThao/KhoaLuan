'use strict';

var domQuery = require('min-dom/lib/query');

var entryFieldDescription = require('./EntryFieldDescription');
var bind = require('lodash/function/bind');
var textField = function(options, defaultParameters) {


  var resource       = defaultParameters,
    label          = options.label || resource.id,
    dataValueLabel = options.dataValueLabel,
    canBeDisabled  = !!options.disabled && typeof options.disabled === 'function',
    canBeHidden    = !!options.hidden && typeof options.hidden === 'function',
    description    = options.description;
    //var onclick=options.onClick;
  resource.html =
    '<div class="bpp-field-wrapper" ' +
    (canBeDisabled ? 'data-disable="isDisabled"' : '') +
    (canBeHidden ? 'data-show="isHidden"' : '') + '>' +
    //'<input  data-action="onClick" value="' + label  + '" type="button" onclick="'+options.onClick+';onClick()"/>'+
    '<input  data-action="onClick" value="' + label  + '" type="button"/>'+
    '<div data-list-entry-container><div class="bpp-field-wrapper bpp-table-row" data-index="0"><input type="hidden" class="bpp-table-row-columns-2 bpp-table-row-removable" id="camunda-table-row-cell-input-value" type="text" name="'+options.modelProperty+'"></div></div>'
    '</div>';

  // add description below text input entry field
  if (description) {
    resource.html += entryFieldDescription(description);
  }

  if (canBeDisabled) {
    resource.isDisabled = function() {
      return options.disabled.apply(resource, arguments);
    };
  }

  if (canBeHidden) {
    resource.isHidden = function() {
      return !options.hidden.apply(resource, arguments);
    };
  }

  resource.cssClasses = ['bpp-textfield'];
  resource.onClick = bind(options.onClick, resource);
  return resource;
};

module.exports = textField;
