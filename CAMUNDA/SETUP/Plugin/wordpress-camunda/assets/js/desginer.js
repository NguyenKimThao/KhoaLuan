var $ = jQuery.noConflict();


$.fn.getType = function () {
    var tagName = this[0].tagName.toLowerCase();
    console.log(tagName)
    if (tagName == "button" || (tagName == "input" && this[0].type == "button")) {
        console.log($(this[0]))
        if($(this[0]).find('.glyphicon-calendar')[0])
            return '';
        return 'button';
    }
    if (this[0].tagName == "INPUT" || this[0].tagName == "input") {
        if ($(this[0]).attr('datepicker-popup')) {
            return 'date';
        }
        if (this[0].type == "checkbox" || this[0].type == "date" || this[0].type == "button") {
            return this[0].type.toLowerCase();
        }
    }
    return this[0].tagName.toLowerCase();
}

var modal = $("#modelfade");
var inputType = "<input class='hidden' type='text' id='data-model' value='input'>";
var objProperties = {
    "label": "<div class='form-group label-wrap'><label for='label-frmb'>Label</label><div class='input-wrap'><input class='fld-label form-control'  id='label-wrap' value='{{label}}'/></div></div>",
    "name": "<div class='form-group name-wrap'><label for='name-frmb'>Name</label><div class='input-wrap'><input class='fld-name form-control' id='name-wrap' value='{{name}}'></div></div>",
    "class": "<div class='form-group className-wrap'><label for='className-frmb'>Class</label> <div class='input-wrap'><input class='fld-className form-control' id='class-wrap' value='{{class}}'></div></div>",
    "id": "<div class='form-group id-wrap'><label for='id-frmb'>ID</label><div class='input-wrap'><input class='fld-value form-control' id='id-wrap' value='{{id}}'></div></div>",
    "value": "<div class='form-group value-wrap'><label for='value-frmb'>Value</label><div class='input-wrap'><input class='fld-value form-control' id='value-wrap' value='{{value}}'></div></div>",
    "placeholder": "<div class='form-group placeholder-wrap'><label for='placeholder-frmb'>Placeholder</label><div class='input-wrap'><input class='fld-placeholder form-control' id='placeholder-wrap' value='{{placeholder}}'></div></div>",
    "width": "<div class='form-group width-wrap'><label for='width-frmb'>Width</label><div class='input-wrap'><input class='fld-placeholder form-control' id='width-wrap' value='{{width}}'></div></div>",
    "header": "<div class='form-group header-wrap'><label for='header-frmb'>Header</label><div class='input-wrap'><input class='fld-placeholder form-control' id='header-wrap' value='{{header}}'></div></div>",
    "add-header": "<div class='form-group header-wrap'><label for='header-frmb'>Header</label><div class='input-wrap'><div id='header-value' class='header-value' id='header-wrap' value=''></div><button type='button' onclick='addHeader()' id='btnAddHeader' class='btn-add-header'>+</button></div></div>",
    "column": "<div class='form-group column-wrap'><label for='column-frmb'>Column</label><div class='input-wrap'><input class='fld-placeholder form-control' id='column-wrap' value='{{column}}'></div></div>",
    "column-value": "<div class='form-group column-wrap'><label for='column-frmb'>Column</label><div class='input-wrap'><input class='fld-placeholder form-control' id='column-value-wrap' value='{{column}}'></div></div>",
    "cam-variable-name": "<div class='form-group cam-variable-name-wrap'><label for='cam-variable-name-frmb'>Cam-name</label><div class='input-wrap'><input class='fld-placeholder form-control' id='cam-variable-name-wrap' value='{{cam-variable-name}}'></div></div>",
    "cam-choices": "<div class='form-group cam-choices-wrap'><label for='cam-choices-frmb'>Cam-choices</label><div class='input-wrap'><input class='fld-placeholder form-control' id='cam-choices-wrap' value='{{cam-choices}}'></div></div>",
    "cam-value": "<div class='form-group cam-value-wrap'><label for='cam-value-frmb'>Cam-value</label><div class='input-wrap'><input class='fld-placeholder form-control' id='cam-value-wrap' value='{{cam-value}}'></div></div>",
    "cam-text": "<div class='form-group cam-text-wrap'><label for='cam-text-frmb'>Cam-text</label><div class='input-wrap'><input class='fld-placeholder form-control' id='cam-text-wrap' value='{{cam-text}}'></div></div>",
    "cam-variable-type": "<div class='form-group subtype-wrap type-cam-variable-type'>" +
        "    <label for='type-input'>Cam-Type</label>" +
        "    <div class='input-wrap'>" +
        "       <select id='cam-variable-type-wrap' class='fld-subtype typelabelposition form-control'>" +
        "            <option value=\"String\" selected>String</option>\n" +
        "            <option value=\"Integer\">Integer</option>\n" +
        "            <option value=\"Date\" >Date</option>" +
        "            <option value=\"Long\">Long</option>\n" +
        "            <option value=\"Double\">Double</option>\n" +
        "            <option value=\"Short\">Short</option>\n" +
        "            <option value=\"Boolean\">Boolean</option>\n" +
        "        </select>" +
        "    </div>" +
        "</div>",
    "typelabelposition": "<div class='form-group subtype-wrap type-input'>" +
        "    <label for='type-input'>Position</label>" +
        "    <div class='input-wrap'>" +
        "       <select id='typelabelposition-wrap' name='subttype-inputype' class='fld-subtype typelabeltion form-control'>" +
        "            <option value='top' selected>Top</option>" +
        "            <option value='leftleft'>Left (Left-aligned)</option>" +
        "            <option value='leftright'>Left (Right-aligned)</option>" +
        "            <option value='rightleft'>Right (Left-aligned)</option>" +
        "            <option value='rightright'>Right (Right-aligned)</option>" +
        "        </select>" +
        "<script>" +
        "$('#typelabelposition-wrap').change(function(){" +
        " var objPosition = $(\"#typelabelposition-wrap\").val();\n" +
        "                if (objPosition == 'top')\n" +
        "                    $(\"#width-wrap\").val(\"100%\");\n" +
        "                else\n" +
        "                    $(\"#width-wrap\").val(\"67%\");" +
        "})" +
        "<\/script>" +
        "    </div>" +
        "</div>",
    "typebutton": "<div class='form-group subtype-wrap type-button'>" +
        "    <label for='type-button'>Type button</label>" +
        "    <div class='input-wrap'>" +
        "        <select id='typebutton-wrap' name='type-button' class='fld-subtype form-control'>" +
        "            <option label='Button' value='button' selected>Button</option>" +
        "            <option label='Submit' value='submit'>Submit</option>" +
        "            <option label='Reset' value='reset'>Reset</option>" +
        "        </select>" +
        "    </div>" +
        "</div>",
    "typeinput": "<div class='form-group subtype-wrap type-input'>" +
        "    <label for='type-input'>Type Input</label>" +
        "    <div class='input-wrap'>" +
        "       <select id='typeinput-wrap' name='subttype-inputype' class='fld-subtype form-control'>" +
        "            <option label='Text Field' value='text' selected>Text field</option>" +
        "            <option label='Password' value='password'>Password</option>" +
        "            <option label='Email' value='email'>Email</option>" +
        "            <option label='Number' value='number'>Number</option>" +
        "            <option label='Color' value='color'>Color</option>" +
        "            <option label='Tel' value='tel'>Tel</option>" +
        "        </select>" +
        "    </div>" +
        "</div>",
    "textarea": "<textarea class='form-group ' style='width:100%' id='textarea-wrap'></textarea>",
    "datepicker-popup": "<div class='form-group subtype-wrap type-input'>" +
    "    <label for='type-input'>Type Input</label>" +
    "    <div class='input-wrap'>" +
    "       <select id='typeinput-wrap' name='subttype-inputype' class='fld-subtype form-control'>" +
    "            <option label='dd-MM-yyyy' value='dd-MM-yyyy' selected>dd-MM-yyyy</option>" +
    "            <option label='MM-dd-yyyy' value='MM-dd-yyyy' >MM-dd-yyyy</option>" +
    "            <option label='yyyy-MM-dd' value='yyyy-MM-dd' >yyyy-MM-dd</option>" +
    "            <option label='yyyy-dd-MM' value='yyyy-dd-MM' >yyyy-dd-MM</option>" +
    "        </select>" +
    "    </div>" +
    "</div>"
};
var objectTypes = {
    "input": {
        "result": " <input class='form-control ' type='{{typeinput}}'>",
        "value": {
            "id": "",
            "label": "Text Field",
            "typelabelposition": "top",
            "name": "",
            "class": "form-control ",
            "value": "",
            "placeholder": "",
            "width": "100%",
            "typeinput": "text",
            "cam-variable-name": "",
            "cam-variable-type": "String",
        }
    },
    "button": {
        "result": " <button type='{{typebutton}}'>{{label}}</button>",
        "value": {
            "id": "",
            "label": "Button Field",
            "name": "",
            "class": "",
            "value": "",
            "width": "",
            "typebutton": "button",
            "cam-variable-name": "",
            "cam-variable-type": "String",
        }
    },
    "select": {
        "result": " <select class='form-control ' ></select>",
        "value": {
            "id": "",
            "label": "Select Field",
            "typelabelposition": "top",
            "name": "",
            "class": "form-control ",
            "placeholder": "",
            "width": "100%",
            "cam-variable-name": "",
            "cam-variable-type": "String",
            "cam-choices": "",
            "cam-value": "",
            "cam-text": "",

        }
    },
    "code": {
        "result": "",
        "value": {
            "textarea": "",
        }
    },
    "table": {
        "value": {
            "id": "",
            "class": "",
            "width": "100%",
            "cam-choices": "",
            "header": "",
            "column": "",
            "cam-variable-name": "",
            // "column-value": "",
            // "add-header": ""

        },
        "result": "<div class=\"panel panel-default templatemo-content-widget white-bg no-padding templatemo-overflow-hidden\"\n" +
            "                    style=\"width:100%\">\n" +
            "                    <div class=\"table-responsive\">\n" +
            "                        <table class=\"table table-striped table-bordered\">\n" +
            "                            <thead>\n" +
            "                            </thead>\n" +
            "                            <tbody>\n" +
            "                            </tbody>\n" +
            "                        </table>\n" +
            "                    </div>\n" +
            "                </div>",
    },
    "p": {
        "result": " <p></p>",
        "value": {
            "id": "",
            "label": "Paragraph Field",
            "typelabelposition": "leftleft",
            "class": "",
            "value": "",
            "cam-variable-name": "",
            "cam-variable-type": "String",
            "cam-choices": "",
            "cam-value": "",
            "cam-text": "",
        }
    },
    "date": {
        "result": " <input class='form-control ' type='text' datepicker-popup='yyyy-MM-dd' is-open='dateFieldOpened'/>",
        "value": {
            "id": "",
            "label": "Text Field",
            "typelabelposition": "top",
            "name": "",
            "class": "form-control ",
            "value": "",
            "placeholder": "",
            "width": "100%",
            "cam-variable-name": "",
            "cam-variable-type": "String",
            "datepicker-popup": 'yyyy-MM-dd'
        }
    },
    "checkbox": {
        "result": " <input class='form-control ' type='checkbox'/>",
        "value": {
            "id": "",
            "label": "Text Field",
            "typelabelposition": "top",
            "name": "",
            "class": "form-control",
            "value": "",
            "placeholder": "",
            "width": "100%",
            "cam-variable-name": "",
            "cam-variable-type": "String",
        }
    }

};
addHeader = function () {
    let comp = $('#header-value');
    let header = window.prompt('Enter header: ');
    let value = (comp.attr('value') || '');
    if (header !== null && header !== '') {
        if (value.split(';').includes(header)) {
            alert(`${header} is exist`);
            return;
        }
        comp.html(`${comp.html()} <div class='header-label' onclick='deleteHeader("${header}")'>${header}</div>`);
        comp.attr('value', (comp.attr('value') || '') + header + ';')
    }
}

deleteHeader = function (val) {
    let comp = $('#header-value');
    let reg = `<div class="header-label" onclick="deleteHeader(&quot;${val}&quot;)">${val}</div>`;
    let value = comp.attr('value');
    value = value.replace(`${val};`, '');
    comp.attr('value', value);
    let text = comp.html().replace(reg, '');
    comp.html(text)
}


objProperties = JSON.parse(JSON.stringify(objProperties));
objectTypes = JSON.parse(JSON.stringify(objectTypes));
var objCurrent;
var objCurrentMove;

HidenDialog = function () {
    ClearDialog();
    modal.modal('toggle');
}
ClearDialog = function () {
    $("#saveDialog").addClass('hidden');
    $("#updateDialog").addClass('hidden');
    $("#form-elements").empty();
}
BeforeShowDialog = function (action) {
    ClearDialog();
    modal.css("display", "block");
    modal.modal({ backdrop: true });
    $(action).removeClass('hidden');
    $("#form-elements").empty();
}
deleteElement = function (id) {
    $(id).parents('.formReview').remove();
}
updateElement = function (id) {
    objCurrent = $(id).parents('.formReview');
    var objLabel = objCurrent.find('.type-label');

    var type = getTypeModel(objCurrent);
    var objElement = getElementObject(objCurrent);
    var data = getDataHTML(objElement, objLabel, type);
    UpdateDialog(type, data);
}
Show = function (str) {
    var date = new Date();
    if (str != 'code') {
        objectTypes[str].value.id = str + date.getTime();
    }
    BeforeShowDialog("#saveDialog");
    var inputTypeShow = $(inputType);
    inputTypeShow.val(str);
    $("#form-elements").append(inputTypeShow);

    ShowDialog(str, objectTypes[str].value);
}
UpdateDialog = function (str, data) {
    BeforeShowDialog("#updateDialog");
    ShowDialog(str, data);
}
ShowDialog = function (type, data) {
    for (var obj in objectTypes[type].value) {
        var objectValue = $(objProperties[obj]);
        objectValue.find("#" + obj + "-wrap").val(data[obj]);
        $("#form-elements").append(objectValue);
    }
}
XulyTiepShowDialog = function (type, res, data) {
    for (var obj in objectTypes[type].value) {
        if (obj.indexOf("type") != -1) {
            res.find("#" + obj + "-wrap").val(data[obj]);
        }
    }
    return res;

}
SaveDialog = function () {
    var id = $("#id-wrap").val();
    if (id && $("#" + id).length) {
        alert(id + " đã tồn tại \n vui lòng chọn id khác");
        return;
    }
    var objData = GetValueData();
    $("#container-components").find("form").append(objData);
    HidenDialog();
}

SaveUpdateDialog = function () {
    var id = $("#id-wrap").val();
    var type = getTypeModel(objCurrent);
    var objElement = getElementObject(objCurrent);
    var idCurrent = objElement.attr('id');
    if (id && id != idCurrent && $("#" + id).length) {
        alert(id + " đã tồn tại \n vui lòng chọn id khác");
        return;
    }

    SetValueData(objCurrent, type);
    HidenDialog();
}
getElementObject = function (objParent) {
    var type = getTypeModel(objParent);
    return $(objParent.find(type));
}
getDataHTML = function (objElement, objLabel, type) {
    var data = {};
    for (var att in objectTypes[type].value) {
        if (att == 'label')
            data[att] = objLabel.html().trim();
        else
            data[att] = converTypeObj(type, att, objElement);
    }
    return data;
}
converTypeObj = function (type, att, objElement) {
    var res = objElement.attr(att);
    switch (att) {
        case "typebutton":
        case "typeinput":
            res = objElement.attr("type");
            break;
    }
    if (res !== undefined)
        return res;
    return objectTypes[type].value[att];
}
getTypeModel = function (objElement) {
    var type = objElement.attr('type-model');
    if (type)
        return type;
    type = 'input';
    var chilr = objElement.children();
    if ($(chilr[0]).getType() === 'label')
        $(chilr[0]).remove();
    $(chilr).each(function (index) {
        if (objectTypes[$(this).getType()]) {
            type = $(this).getType();
            return;
        }
    });
    return type;
}
SetValueTableData = function (objModel) {
    var valChose = $("#cam-choices-wrap").val();
    if (valChose === undefined)
        return;
    var str = $("#header-wrap").val();
    if (str.endsWith(";"))
        str = str.substr(0, str.length - 1);
    var header = str.split(";");
    // objModel.append($('<select class="hidden" cam-variable-name="' + valChose + '" cam-variable-type="String" cam-choices="' + valChose + '" ></select>'));
    objModel.find('table').attr('cam-choices',valChose);
    var trHeader = objModel.find("thead");
    
    var strHeader = "<tr>";
    for (var key in header) {
        strHeader = strHeader + "\n" + "<th>" + header[key] + "</th>";
    }
    strHeader = strHeader + "</tr>";
    if (trHeader)
        trHeader.append($(strHeader));
    var chose = "<tr>";

    str = $("#column-wrap").val();
    if (str.endsWith(";"))
        str = str.substr(0, str.length - 1);
    var column = str.split(";");
    for (var key in column) {
        if (column[key].startsWith("[") && column[key].endsWith("]")) {
            str = column[key].substr(1, column[key].length - 2);
            col = str.split(",");
            chose = chose + '\n<td> <button type="button" class="templatemo-blue-button" ng-click="actionForm(camForm,item,\'' + col[1] + '\',\'' + col[2] + '\')">' + col[0] + '</button></td>';
        }
        else
            chose = chose + "\n" + "<td><p>{" + column[key] + "}</p></td>";
    }

    chose += "</tr>";
    objModel.find("tbody").append($(chose));
}

// SetValueTableData = function (objModel) {
//     objModel.find('table').attr('cam-choices', $("#cam-choices-wrap").val());
//     var valChose = $("#cam-choices-wrap").val();
//     if (valChose === undefined)
//         return;
//     var str = $("#header-wrap").attr('value');
//     if (str.endsWith(";"))
//         str = str.substr(0, str.length - 1);
//     var header = str.split(";");
//     var trHeader = objModel.find("thead");
//     var strHeader = "<tr>";
//     for (var key in header) {
//         strHeader = strHeader + "\n" + "<th>" + header[key] + "</th>";
//     }
//     strHeader = strHeader + "</tr>";
//     if (trHeader)
//         trHeader.append($(strHeader));
//     var chose = "<tr>";
//     str = $("#column-wrap").val();
//     if (str.endsWith(";"))
//         str = str.substr(0, str.length - 1);
//     var column = str.split(";");
//     for (var key in column) {
//         if (column[key].startsWith("[") && column[key].endsWith("]")) {
//             str = column[key].substr(1, column[key].length - 2);
//             col = str.split(",");
//             chose = chose + '\n<th> <button type="button" class="templatemo-blue-button" ng-click="actionForm(camForm,item,\'' + col[1] + '\',\'' + col[2] + '\')">' + col[0] + '</button></th>';
//         }
//         else
//             chose = chose + "\n" + "<th>{" + column[key] + "}</th>";
//     }

//     chose += "</tr>";
//     objModel.find("tbody").append($(chose));
// }

SetValueData = function (objModel, type) {
    var label = objModel.find('.type-label');
    var objElement = getElementObject(objModel);
    for (var obj in objectTypes[type].value) {
        if ($("#" + obj + "-wrap").val() && $("#" + obj + "-wrap").val() != "") {
            if (obj !== 'type' && obj.indexOf("type") == 0)
                objElement.attr("type", $("#" + obj + "-wrap").val());
            else
                objElement.attr(obj, $("#" + obj + "-wrap").val());
        }
    }
    XulyTruocTiepSetValueData(type, label, objElement);
}
XulyTruocTiepSetValueData = function (type, label, objElement) {
    if (type == 'table') {
    }
    else {
        if (type == 'date') {
            objElement.attr('type', 'date');
        }
        if (type == 'button') {
            label.addClass('hidden');
            objElement.html($("#label-wrap").val());
        }
        else {
            if (type == 'p') {
                var vl = $("#value-wrap").val();
                if ($("#cam-variable-name-wrap").val())
                    vl = "{{" + $("#cam-variable-name-wrap").val() + "}}";
                if ($("#cam-choices-wrap").val())
                    vl = $("#cam-text-wrap").val();
                objElement.html(vl);
            }

            var objPosition = $("#typelabelposition-wrap").val();
            switch (objPosition) {
                case "top":
                    label.attr('style', "");
                    objElement.attr('style', "width: 100%");
                    objElement.attr('typelabelposition', 'top');
                    break;
                case "leftleft":
                    label.attr('style', "float: left; width: 30%; margin-right: 3%; text-align: left;");
                    objElement.attr('style', "width: 67%; ");
                    objElement.attr('typelabelposition', 'leftleft');
                    break;
                case "leftright":
                    label.attr('style', "float: left; width: 30%; margin-right: 3%; text-align: right;");
                    objElement.attr('style', "width: 67%; ");
                    objElement.attr('typelabelposition', 'leftright');
                    break;
                case "rightleft":
                    label.attr('style', "float: right; width: 30%; margin-left: 3%; text-align: left;");
                    objElement.attr('style', "width: 67%;");
                    objElement.attr('typelabelposition', 'rightleft');
                    break;
                case "rightright":
                    label.attr('style', "float: right; width: 30%; margin-left: 3%; text-align: right;");
                    objElement.attr('style', "width: 67%; ");
                    objElement.attr('typelabelposition', 'rightright');
                    break;
            }
            if (type != 'checkbox')
                objElement.addClass('form-control');
        }
        label.html($("#label-wrap").val());
        label.attr("for", $("#name-wrap").val());
        objElement.css({ 'width': $("#width-wrap").val() });
    }
}
renderCodeHTML = function () {
    return "<div class='form-group formReview' draggable='true'   ondragstart='drag(event)' ondragover='allowDrop(event)' ondrop='drop(event)'>" +
        preview +
        "   <label  class='fb-date-label type-label'></label>" +
        $("#textarea-wrap").val() +
        "</div>";
}
renderHTML = function (type) {
    let text = `<div class='form-group formReview' draggable='true'   ondragstart='drag(event)' ondragover='allowDrop(event)' ondrop='drop(event)'> ${preview} <label  class='fb-date-label type-label'></label> ${objectTypes[type].result} </div>`
    // return "<div class='form-group formReview' draggable='true'   ondragstart='drag(event)' ondragover='allowDrop(event)' ondrop='drop(event)'>" +
    //     preview +
    //     "   <label  class='fb-date-label type-label'></label>" +
    //     objectTypes[type].result +
    //     "</div>"
    //     ;
    return text;
}
GetValueData = function () {
    var type = $("#data-model").val();
    if (type == 'code') {
        return $(renderCodeHTML());
    }
    var objectElemnt = $(renderHTML(type));
    if (type == 'table') {
        SetValueTableData(objectElemnt);
    }
    else {
        SetValueData(objectElemnt, type);
    }
    if (type === 'date')
        objectElemnt.append(`<span class="input-group-btn">
        <button type="button" class="btn btn-default" ng-click="open($event)">
          <i class="glyphicon glyphicon-calendar"></i>
        </button>
      </span>`)
    return objectElemnt;
}


getClassItem = function (ev) {
    var objNew = $(ev.target);
    if (!objNew.hasClass('formReview')) {
        objNew = $(objNew.parents('.formReview'));
    }
    return objNew;
}

allowDrop = function (ev) {
    ev.preventDefault();
}

drag = function (ev) {
    objCurrentMove = getClassItem(ev);
}

drop = function (ev) {
    ev.preventDefault();
    var objNew = getClassItem(ev);
    $(objCurrent)
    if (objCurrentMove == objNew)
        console.log('vang');

    var objNewClone = objNew.clone();
    var objCurrentClone = objCurrentMove.clone();
    objCurrentMove.replaceWith(objNewClone);
    objNew.replaceWith(objCurrentClone);
}
