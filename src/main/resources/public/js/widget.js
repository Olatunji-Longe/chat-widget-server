/**
 * Created by olatunji on 9/11/17.
 */

(function() {

    // Localize jQuery variable
    var jQuery;

    /******** Load jQuery if not present *********/
    if (window.jQuery === undefined || window.jQuery.fn.jquery !== '1.10.2') {
        var script_tag = document.createElement('script');
        script_tag.setAttribute("type","text/javascript");
        // script_tag.setAttribute("src", "http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js");
        script_tag.setAttribute("src", "http://localhost:4567/js/jquery.min.js");
        if (script_tag.readyState) {
            script_tag.onreadystatechange = function () { // For old versions of IE
                if (this.readyState == 'complete' || this.readyState == 'loaded') {
                    scriptLoadHandler();
                }
            };
        } else {
            script_tag.onload = scriptLoadHandler;
        }
        // Try to find the head, otherwise default to the documentElement
        (document.getElementsByTagName("head")[0] || document.documentElement).appendChild(script_tag);
    } else {
        // The jQuery version on the window is the one we want to use
        jQuery = window.jQuery;
        main();
    }

    /******** Called once jQuery has loaded ******/
    function scriptLoadHandler() {
        // Restore $ and window.jQuery to their previous values and store the
        // new jQuery in our local jQuery variable
        jQuery = window.jQuery.noConflict(true);
        // Call our main function
        main();
    }

    function loadCss(){
        var headElement = (document.getElementsByTagName("head")[0] || document.documentElement);
        var cssLinks = [
            "http://localhost:4567/css/bootstrap.css",
            "http://localhost:4567/css/popchat.css",
            "http://localhost:4567/css/main.css",
            "http://localhost:4567/css/font-awesome.min.css",
            "http://localhost:4567/css/font.css"
        ];
        for(var n = 0; n < cssLinks.length; n++){
            var link_tag = document.createElement('link');
            link_tag.setAttribute("rel", "stylesheet");
            link_tag.setAttribute("type", "text/css");
            link_tag.setAttribute("href", cssLinks[n]);
            headElement.appendChild(link_tag);
        }
    }

    function loadJavascripts(){
        var headElement = (document.getElementsByTagName("head")[0] || document.documentElement);
        var scriptSources = [
            "http://localhost:4567/js/bootstrap.js",
            "http://localhost:4567/js/global.js",
            "http://localhost:4567/js/websocket-messaging.js",
            "http://localhost:4567/js/popchat.js"
        ];
        for(var n = 0; n < scriptSources.length; n++){
            var script_tag = document.createElement('script');
            script_tag.setAttribute("type", "text/javascript");
            script_tag.setAttribute("src", scriptSources[n]);
            headElement.appendChild(script_tag);
        }
    }

    /******** Our main function ********/
    function main() {
        jQuery(document).ready(function($) {

            /******* Load CSS *******/
            loadCss();

            /******* Load Javascripts *******/
            loadJavascripts();

            /******* Load HTML *******/
            var widget_url = "http://localhost:4567/widget?callback=?";
            $.ajax({
                url: widget_url,
                type: 'GET',
                dataType: 'json',
                success: onSuccess,
                error: onError
            });

            function onSuccess(data){
                console.log(data);
                var response = $.parseHTML(data.content);
                $('#webchat-widget-container').html($(response).html());
            }

            function onError(){
                alert('Failed!');
            }
        });
    }
})(); // We call our anonymous function immediately