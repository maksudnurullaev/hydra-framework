/* Initial global setups */
if (Globals == null) {
    var Globals = {
        'version': '0.0.1'
    };
};
/* Values */
Globals.DivContentID = "content";
Globals.DivInvisibleID = "stack";
/* JS Loader */
Globals.loadJS = function (pathToJS, onLoadFn) {
    new Asset.javascript(pathToJS, {
        onload: onLoadFn
    });
};
/* Global initializator */
window.addEvent('domready', function () {
    Globals.initializeAll();
});
/* Initialize all stages of page loading */
Globals.initializeAll = function () {
    // Initialize load stages
    Globals.loadStages();
};
/* Defines is stages loaded */
Globals.isLoadedStage1 = function () {
    return Globals.isLoadedStage11() && Globals.isLoadedStage12() && Globals.isLoadedStage13();
};
Globals.isLoadedStage11 = function () {
    return typeof DWREngine != "undefined";
};
Globals.isLoadedStage12 = function () {
    return typeof DWRUtil != "undefined";
};
Globals.isLoadedStage13 = function () {
    return typeof MessageHandler != "undefined";
};
/* JS loading stages */
Globals.loadStages = function () {
    Globals.loadStage1();
};
Globals.loadStage1 = function () {
    if (!Globals.isLoadedStage1()) {
        Globals.loadStage11()
    };
};
Globals.loadStage11 = function () {
    if (!Globals.isLoadedStage11()) {
        Globals.loadJS("dwr/engine.js", Globals.loadStage12);
    }
};
Globals.loadStage12 = function () {
    if (!Globals.isLoadedStage12()) {
        Globals.loadJS("dwr/util.js", Globals.loadStage13);
    }
};
Globals.loadStage13 = function () {
    if (!Globals.isLoadedStage13()) {
        Globals.loadJS("dwr/interface/MessageHandler.js", Globals.loadStage2);
    }
};
Globals.loadStage2 = function () {
    // Check that all stages passed #1
    if (!Globals.isLoadedStage13()) {
        Globals.debug("ERROR: Could not load stage #1");
        return;
    };
    // Clear old stylesheets if exist
    Globals.clearStyleSheets();
    // Get initial html body
    Globals.sendMessage({
        handler: 'General',
        action: 'getInitialHTMLElements',
        dest: 'body'
    });
};
/* CSS files managements */
Globals.clearStyleSheets = function () {
    Array.each(document.head.getElementsByTagName('link'), function (item) {
        if (item.rel && item.rel.test("stylesheet", "i") && item.href) {
            item.href = "";
        };
    });
};
/* Clears elements content */
Globals.clearContent = function (destId, saveTag) {
    if(!$(destId)){alert("Could not find DOM element by Id: " + destId); return false; }
    if (arguments.length == 1) {
        $(destId).innerHTML = "";
        return false;
    };
    $each($(destId).childNodes, function (elem, elemIndex) {
        if ($(elem) && elem.nodeName && elem.nodeName.test(saveTag, "i") && elem.id) {
            $(Globals.DivInvisibleID).appendChild(elem);
        };
    });
    $(destId).innerHTML = "";
    return false;
};
/* Set disables for DOM elements by css path and disable state */
Globals.disableElements = function (path, disableState) {
    Array.each($$(path), function (elem) {
        elem.set('disabled', disableState);
    });
};
/* Set html content by contents map */
Globals.setHtmlContents = function (contentsMap) {
    Hash.each(contentsMap, function (htmlContent, elemId) {
        if ($(elemId) && $(elemId).innerHTML != undefined) {
            $(elemId).innerHTML = htmlContent;
        };
    });
};
/* Test DOM object existance */
Globals.chk = function (obj) {
    return !!(obj || obj === 0);
};
/* Send message to server*/
Globals.sendMessage = function (data) {
    var message = {
        data: data
    };
    Globals.disableElements('#navlist a', true);
    MessageHandler.sendMessage(message, Globals.applyIncomingMessages);
};
/* GLOBAL - receive message(s) */
Globals.applyIncomingMessages = function (messages) {
    Array.each(messages, function (message, messageIndex) {
        // check for error
        if (Globals.chk(message.error)) {
            alert(message.error);
            return;
        };
        if (Globals.chk(message.styleSheets)) {
            message.styleSheets.each(function (cssPath) {
                Asset.css(cssPath, null);
            });
        };
        // check for html contents
        if (Globals.chk(message.htmlContents)) {
            Globals.setHtmlContents(message.htmlContents);
        };
    });
    Globals.disableElements('#navlist a', false);
};

/******** GLOBAL NAVIGATION ********/

if (GlobalsNav == null) {
    var GlobalsNav = {
        'version': '0.0.1',
        selected: '__none___'
    };
};
GlobalsNav.onClick = function (elm) {

    /* Change selection */
    if ($("title")) {
        $("title").innerHTML = elm.innerHTML;
    }

    /* Move child elements to stack & clear context */
    Globals.clearContent(Globals.DivContentID, 'div');

    /* Get content */
    var requestDivId = elm.id + ".content";
    if (!$(requestDivId)) { /* If not found, request it from servet */
        Globals.sendMessage({
            handler: 'General',
            action: 'getTextByKey',
            key: requestDivId,
            dest: Globals.DivContentID
        });
    } else { /* From Stack */
        $(Globals.DivContentID).appendChild($(requestDivId));
    }

    /* Save selected menu ID */
    GlobalsNav.selected = elm.id;

    return false;
};