'use strict';

var domQuery = require('min-dom/lib/query');

var entryFieldDescription = require('./EntryFieldDescription');
var bind = require('lodash/function/bind');
var $=require('jquery');
var textField = function(options, defaultParameters) {
  var resource       = defaultParameters,
    label          = options.label || resource.id,
    dataValueLabel = options.dataValueLabel,
    canBeDisabled  = !!options.disabled && typeof options.disabled === 'function',
    canBeHidden    = !!options.hidden && typeof options.hidden === 'function',
    description    = options.description,
    selectOptions= options.selectOptions;
  var htmlOption='';
  var attributes=options.attributes;
  var count=0;
  for(var i=0;i<selectOptions.length;i++){
    var value='';
    if(attributes) {
      if(attributes==selectOptions[i].name){
        value='selected';
        count++;
      }
    }
    htmlOption+='<option value="'+selectOptions[i].name+'" '+value+'>'+selectOptions[i].value+'</option>' ;
  }
  if(count==0&&attributes){
    htmlOption+='<option value="'+attributes+'" '+value+'>'+attributes+'</option>' ;
  }
  if(selectOptions.length==0){
    for(var i=0;i<5;i++){
      htmlOption+='<option value=""></option>' ;
    }
  }
  resource.html =
    '<label for="camunda-' + resource.id + '" ' +
    (canBeDisabled ? 'data-disable="isDisabled" ' : '') +
    (canBeHidden ? 'data-show="isHidden" ' : '') +
    (dataValueLabel ? 'data-value="' + dataValueLabel + '"' : '') + '>'+ label +'</label>' +
    '<div class="bpp-field-wrapper" ' +
    (canBeDisabled ? 'data-disable="isDisabled"' : '') +
    (canBeHidden ? 'data-show="isHidden"' : '') +
    '>' +
    '<select id="camunda-' + resource.id + '" name="' + options.modelProperty+'" data-action="onClick1" >' +
    htmlOption+
    '</select>'+
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
  if(options.onClick) {
    resource.onClick1=bind(options.onClick,resource);
  }

  return resource;
};

module.exports = textField;
