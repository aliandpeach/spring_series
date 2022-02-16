(function( window, undefined ) {
    function common(){
    }
    common.prototype.download = function (url, params){
        params = params || {};
        var iframeId = "iframe_id" + new Date().getTime();
        var iframeEl = document.getElementById(iframeId);
        if (!iframeEl) {
            var _iframe = document.createElement("iframe");
            _iframe.setAttribute("id", iframeId);
            _iframe.setAttribute("name", iframeId);
            _iframe.setAttribute("style", "width:  0px; height: 0px; display: none; ");
            _iframe.setAttribute("src", "about: blank");
            top.document.getElementsByTagName("body")[0].appendChild(_iframe);
            iframeEl = document.getElementById(iframeId);
        }
        var iframeDoc = iframeEl.contentDocument;
        iframeDoc.open();
        iframeDoc.write('<form method="post" action="' + url + '" >');
        iframeDoc.close();
        var formEl = iframeDoc.getElementsByTagName("form");

        for (var _key in params) {
            var _input = document.createElement("input");
            _input.setAttribute("name", _key);
            _input.setAttribute("value", params[_key]);
            formEl[0].appendChild(_input);
        }
        formEl[0].submit();
    };

    common.prototype._http = (function () {
        function _ajax (url, type, data, download){
            var xhr = new XMLHttpRequest();
            var _promise = new Promise(function (resolve, reject) {
                xhr.open(type, url, true); // true -> async
                xhr.onreadystatechange = () => {
                    if (xhr.readyState === 4) {
                        // console.log("request ready, response ready");
                    }
                };
                xhr.onload = function () {
                    if (this.status === 200) {
                        if (download) {
                            var blob = this.response;
                            resolve(blob);
                            return;
                        }
                        var result = JSON.parse(this.responseText);
                        resolve(result);
                    }
                };
                xhr.onerror = function (e) {
                    reject(e);
                };
                xhr.timeout = 0; // 设置超时时间, 0表示永不超时
                xhr.responseType = !download ? 'json' : 'blob'; // 'json' 'text' 'document'
                xhr.setRequestHeader("Content-Type","application/json");
                type && type.toUpperCase() === 'GET' ? xhr.send() : xhr.send(data); // null || new FormData || 'a=1&b=2' || 'json'
            });
            return _promise
        };
        return {ajax : _ajax}; // _http.ajax('', '', '').then(console.log, console.error);
    })();

    common.prototype._request = {
        ajax : function _ajax (url, type, data){
            var xhr = new XMLHttpRequest();
            var _promise = new Promise(function (resolve, reject) {
                xhr.open(type, url, true);
                xhr.onreadystatechange = (e) => {
                    if (xhr.readyState === 4) {
                        // console.log("request ready, response ready");
                    }
                };
                xhr.onload = function (e) {
                    if (this.status === 200) {
                        var result = JSON.parse(this.responseText);
                        resolve(result);
                    }
                };
                xhr.onerror = function (e) {
                    reject(e);
                };
                xhr.timeout = 0; // 设置超时时间, 0表示永不超时
                // xhr.responseType = ''; // 'json' 'text' 'document'
                // xhr.setRequestHeader("Content-Type","application/x-www-form-urlencoded;");
                type && type.toUpperCase() === 'GET' ? xhr.send() : xhr.send(data); // null || new FormData || 'a=1&b=2' || 'json'
            });
            return _promise
        }
    };
    window.common = new common();
})(window);