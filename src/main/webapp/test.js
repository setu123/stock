Ext.require(["Ext.grid.*", "Ext.data.*", "Ext.slider.Multi", "Ext.toolbar.Spacer", "Ext.ux.grid.FiltersFeature"]);
(Ext.cmd.derive("DataObject", Ext.data.Model, {idProperty: "id", fields: [{name: "id", type: "int"}, {name: "code", type: "string"}, {name: "sector", type: "string"}, {name: "category", type: "string"}, {name: "market_lot", type: "int"}, {name: "face_value", type: "int"}, {name: "nav", type: "float"}, {name: "lastprice", type: "float"}, {name: "open", type: "float"}, {name: "high", type: "float"}, {name: "low", type: "float"}, {name: "pchange", type: "float"}, {name: "change", type: "float"}, {name: "pe", type: "float"}, {name: "eps", type: "float"}, {name: "volume", type: "int"}, {name: "value", type: "float"}, {name: "trade", type: "float"}, {name: "ycp", type: "float"}]}, 0, 0, 0, 0, 0, 0, [0, "DataObject"], 0));
Ext.onReady(function () {
    var b = Ext.create("Ext.data.Store", {model: "DataObject", autoLoad: true, proxy: {type: "ajax", url: "/grids/watch", reader: {type: "json", root: "maingrid"}}, sorters: {property: "sector", direction: "ASC"}, groupField: "sector", pageSize: 350});
    function a(j) {
        if (j > 0) {
            return'<span style="color:green;">' + j + "%</span>"
        } else {
            if (j < 0) {
                return'<span style="color:red;">' + j + "%</span>"
            }
        }
        return j
    }
    function i(j) {
        if (j > 0) {
            return'<span style="color:green;">' + j + "</span>"
        } else {
            Ext.require(['Ext.grid.*', 'Ext.data.*']);
            Ext.define('DataObject', {extend: 'Ext.data.Model', idProperty: 'id', fields: [{name: 'id', type: 'int'}, {name: 'code', type: 'string'}, {name: 'sector', type: 'string'}, {name: 'category', type: 'string'}, {name: 'market_lot', type: 'int'}, {name: 'face_value', type: 'int'}, {name: 'nav', type: 'float'}, {name: 'lastprice', type: 'float'}, {name: 'open', type: 'float'}, {name: 'high', type: 'float'}, {name: 'low', type: 'float'}, {name: 'pchange', type: 'float'}, {name: 'change', type: 'float'}, {name: 'pe', type: 'float'}, {name: 'eps', type: 'float'}, {name: 'volume', type: 'int'}, {name: 'value', type: 'float'}, {name: 'trade', type: 'float'}, {name: 'ycp', type: 'float'}]});
            Ext.onReady(function () {
                var url = {local: 'grid-filter.json', remote: '/grids/watch'};
                function pctChange(val) {
                    if (val > 0) {
                        return'<span style="color:green;">' + val + '%</span>';
                    } else if (val < 0) {
                        return'<span style="color:red;">' + val + '%</span>';
                    }
                    return val;
                }
                function colorChange(val) {
                    if (val > 0) {
                        return'<span style="color:green;">' + val + '</span>';
                    } else if (val < 0) {
                        return'<span style="color:red;">' + val + '</span>';
                    }
                    return val;
                }
                var mainGridStore = Ext.create('Ext.data.Store', {model: 'DataObject', proxy: {type: 'ajax', url: url.remote, reader: {type: 'json', root: 'maingrid'}}, groupField: 'sector', autoLoad: true, listeners: {load: function (store, records, successful) {
                            firstGridStore.loadRawData(store.proxy.reader.jsonData);
                            secondGridStore.loadRawData(store.proxy.reader.jsonData);
                            thirdGridStore.loadRawData(store.proxy.reader.jsonData);
                        }}});
                var groupingFeature = Ext.create('Ext.grid.feature.Grouping', {groupHeaderTpl: '{name} ({rows.length} Item{[values.rows.length > 1 ? "s" : ""]})'});
                var mainGrid = Ext.create('Ext.grid.Panel', {multiSelect: true, alias: 'widget.mainGrid', viewConfig: {plugins: {ptype: 'gridviewdragdrop', dragGroup: 'mainGridDDGroup', dropGroup: 'firstGridDDGroup'}, listeners: {drop: function (node, data, dropRec, dropPosition) {
                                var dropOn = dropRec ? ' ' + dropPosition + ' ' + dropRec.get('name') : ' on empty view';
                            }}}, store: mainGridStore, features: [groupingFeature], columns: [{header: 'id', readOnly: true, dataIndex: 'id', width: 70, hidden: true}, {text: 'Scrips', flex: 1, tdCls: 'task', sortable: true, dataIndex: 'code', hideable: false, summaryType: 'count', width: 100, items: {xtype: 'textfield', flex: 1, margin: 2, enableKeyEvents: true, listeners: {keyup: function () {
                                        var store = this.up('tablepanel').store;
                                        store.clearFilter();
                                        if (this.value) {
                                            store.filter({property: 'code', value: this.value, anyMatch: true, caseSensitive: false});
                                        }
                                    }, buffer: 500}}}, {header: 'Sector', dataIndex: 'sector', width: 200, hidden: true}, {header: 'Category', readOnly: true, dataIndex: 'category', width: 50, hidden: true}, {header: 'LastPrice', dataIndex: 'lastprice', width: 60, readOnly: true}, {header: 'Open', dataIndex: 'open', width: 60, readOnly: true}, {header: 'high', dataIndex: 'high', width: 60, readOnly: true}, {header: 'low', dataIndex: 'low', width: 60, readOnly: true}, {header: 'ycp', dataIndex: 'ycp', width: 60, readOnly: true, hidden: false}, {header: 'Change', renderer: colorChange, dataIndex: 'change', width: 60, readOnly: true}, {header: 'Change%', renderer: pctChange, dataIndex: 'pchange', width: 70, readOnly: true}, {header: '(D&A) P/E', dataIndex: 'pe', width: 60, readOnly: true}, {header: '(D&A) EPS', dataIndex: 'eps', width: 60, readOnly: true}, {header: 'volume', dataIndex: 'volume', width: 70, readOnly: true}, {header: 'Value(mn)', dataIndex: 'value', width: 70, readOnly: true}, {header: 'trade', dataIndex: 'trade', width: 70, readOnly: true}, {header: 'Market lot', dataIndex: 'market_lot', width: 60, readOnly: true, hidden: true}, {header: 'Face value', dataIndex: 'face_value', width: 60, readOnly: true, hidden: true}, {header: 'NAV', dataIndex: 'nav', width: 60, readOnly: true}], stripeRows: true, title: 'Main Grid', margins: '0 2 0 0'});
                var firstGridStore = Ext.create('Ext.data.Store', {model: 'DataObject', proxy: {type: 'memory', reader: {type: 'json', root: 'firstgrid', fields: ['id', 'code', 'sector', 'category', 'market_lot', 'face_value', 'nav', 'lastprice', 'open', 'high', 'low', 'pchange', 'change', 'pe', 'eps', 'volume', 'value', 'trade', 'ycp']}}});
                var firstGrid = Ext.create('Ext.grid.Panel', {viewConfig: {plugins: {ptype: 'gridviewdragdrop', dragGroup: 'firstGridDDGroup', dropGroup: 'mainGridDDGroup'}, listeners: {drop: function (node, data, dropRec, dropPosition) {
                                var dropOn = dropRec ? ' ' + dropPosition + ' ' + dropRec.get('name') : ' on empty view';
                            }}, emptyText: "Drag a scrip from Main Grid and drop here to watch. You can configure the column and hide unnecessary column or rearrange the column position too", deferEmptyText: false}, store: firstGridStore, closable: true, columns: [{header: 'id', readOnly: true, dataIndex: 'id', width: 70, hidden: true}, {header: 'Scrips', dataIndex: 'code', width: 80}, {header: 'Sector', dataIndex: 'sector', width: 200, hidden: true}, {header: 'Category', readOnly: true, dataIndex: 'category', width: 50, hidden: true}, {header: 'LastPrice', dataIndex: 'lastprice', width: 60, readOnly: true}, {header: 'Change', renderer: colorChange, dataIndex: 'change', width: 60, readOnly: true}, {header: 'Change%', renderer: pctChange, dataIndex: 'pchange', width: 70, readOnly: true}, {header: 'volume', dataIndex: 'volume', width: 70, readOnly: true}, {header: 'Open', dataIndex: 'open', width: 60, readOnly: true}, {header: 'high', dataIndex: 'high', width: 60, readOnly: true}, {header: 'low', dataIndex: 'low', width: 60, readOnly: true}, {header: 'ycp', dataIndex: 'ycp', width: 60, readOnly: true, hidden: true}, {header: '(D&A) P/E', dataIndex: 'pe', width: 60, readOnly: true, hidden: true}, {header: '(D&A) EPS', dataIndex: 'eps', width: 60, readOnly: true, hidden: true}, {header: 'Value(mn)', dataIndex: 'value', width: 70, readOnly: true}, {header: 'trade', dataIndex: 'trade', width: 70, readOnly: true, hidden: true}, {header: 'Market lot', dataIndex: 'market_lot', width: 60, readOnly: true, hidden: true}, {header: 'Face value', dataIndex: 'face_value', width: 60, readOnly: true, hidden: true}, {header: 'NAV', dataIndex: 'nav', width: 60, readOnly: true, hidden: true}], stripeRows: true, title: 'First Watch List', margins: '0 0 0 3'});
                var secondGridStore = Ext.create('Ext.data.Store', {model: 'DataObject', proxy: {type: 'memory', reader: {type: 'json', root: 'secondgrid', fields: ['id', 'code', 'sector', 'category', 'market_lot', 'face_value', 'nav', 'lastprice', 'open', 'high', 'low', 'pchange', 'change', 'pe', 'eps', 'volume', 'value', 'trade', 'ycp']}}});
                var secondGrid = Ext.create('Ext.grid.Panel', {viewConfig: {plugins: {ptype: 'gridviewdragdrop', dragGroup: 'secondGridDDGroup', dropGroup: 'mainGridDDGroup'}, listeners: {drop: function (node, data, dropRec, dropPosition) {
                                var dropOn = dropRec ? ' ' + dropPosition + ' ' + dropRec.get('name') : ' on empty view';
                            }}, emptyText: "নিচের টেবিল থেকে শেয়ার টেনে এনে বক্সগুলোতে ফেলুন। একসাথে একাধিক টেনে আনতে পারবেন। সেক্ষেত্রে Ctrl  চেপে ধরুন", deferEmptyText: false}, store: secondGridStore, closable: true, columns: [{header: 'id', readOnly: true, dataIndex: 'id', width: 70, hidden: true}, {header: 'Scrips', dataIndex: 'code', width: 80}, {header: 'Sector', dataIndex: 'sector', width: 200, hidden: true}, {header: 'Category', readOnly: true, dataIndex: 'category', width: 50, hidden: true}, {header: 'LastPrice', dataIndex: 'lastprice', width: 60, readOnly: true}, {header: 'Change', renderer: colorChange, dataIndex: 'change', width: 60, readOnly: true}, {header: 'Change%', renderer: pctChange, dataIndex: 'pchange', width: 70, readOnly: true}, {header: 'volume', dataIndex: 'volume', width: 70, readOnly: true}, {header: 'Open', dataIndex: 'open', width: 60, readOnly: true}, {header: 'high', dataIndex: 'high', width: 60, readOnly: true}, {header: 'low', dataIndex: 'low', width: 60, readOnly: true}, {header: 'ycp', dataIndex: 'ycp', width: 60, readOnly: true, hidden: true}, {header: '(D&A) P/E', dataIndex: 'pe', width: 60, readOnly: true, hidden: true}, {header: '(D&A) EPS', dataIndex: 'eps', width: 60, readOnly: true, hidden: true}, {header: 'Value(mn)', dataIndex: 'value', width: 70, readOnly: true}, {header: 'trade', dataIndex: 'trade', width: 70, readOnly: true, hidden: true}, {header: 'Market lot', dataIndex: 'market_lot', width: 60, readOnly: true, hidden: true}, {header: 'Face value', dataIndex: 'face_value', width: 60, readOnly: true, hidden: true}, {header: 'NAV', dataIndex: 'nav', width: 60, readOnly: true, hidden: true}], stripeRows: true, title: 'Second Watch List', margins: '0 0 0 3'});
                var thirdGridStore = Ext.create('Ext.data.Store', {model: 'DataObject', proxy: {type: 'memory', reader: {type: 'json', root: 'thirdgrid', fields: ['id', 'code', 'sector', 'category', 'market_lot', 'face_value', 'nav', 'lastprice', 'open', 'high', 'low', 'pchange', 'change', 'pe', 'eps', 'volume', 'value', 'trade', 'ycp']}}});
                var thirdGrid = Ext.create('Ext.grid.Panel', {viewConfig: {plugins: {ptype: 'gridviewdragdrop', dragGroup: 'thirdGridStore', dropGroup: 'mainGridDDGroup'}, listeners: {drop: function (node, data, dropRec, dropPosition) {
                                var dropOn = dropRec ? ' ' + dropPosition + ' ' + dropRec.get('name') : ' on empty view';
                            }}, emptyText: "উপরের কলামগুলোর অবস্থান পরিবর্তন করা যায়। যেকোন একটি কলাম চেপে ধরে টেনে আনুন আপনার পছন্দমত অবস্থানে।", deferEmptyText: false}, closable: true, store: thirdGridStore, columns: [{header: 'id', readOnly: true, dataIndex: 'id', width: 70, hidden: true}, {header: 'Scrips', dataIndex: 'code', width: 80}, {header: 'Sector', dataIndex: 'sector', width: 200, hidden: true}, {header: 'Category', readOnly: true, dataIndex: 'category', width: 50, hidden: true}, {header: 'LastPrice', dataIndex: 'lastprice', width: 60, readOnly: true}, {header: 'Change', renderer: pctChange, dataIndex: 'change', width: 60, readOnly: true}, {header: 'Change%', renderer: pctChange, dataIndex: 'pchange', width: 70, readOnly: true}, {header: 'volume', dataIndex: 'volume', width: 70, readOnly: true}, {header: 'Open', dataIndex: 'open', width: 60, readOnly: true}, {header: 'high', dataIndex: 'high', width: 60, readOnly: true}, {header: 'low', dataIndex: 'low', width: 60, readOnly: true}, {header: 'ycp', dataIndex: 'ycp', width: 60, readOnly: true, hidden: true}, {header: '(D&A) P/E', dataIndex: 'pe', width: 60, readOnly: true, hidden: true}, {header: '(D&A) EPS', dataIndex: 'eps', width: 60, readOnly: true, hidden: true}, {header: 'Value(mn)', dataIndex: 'value', width: 70, readOnly: true}, {header: 'trade', dataIndex: 'trade', width: 70, readOnly: true, hidden: true}, {header: 'Market lot', dataIndex: 'market_lot', width: 60, readOnly: true, hidden: true}, {header: 'Face value', dataIndex: 'face_value', width: 60, readOnly: true, hidden: true}, {header: 'NAV', dataIndex: 'nav', width: 60, readOnly: true, hidden: true}], stripeRows: true, title: 'Third Watch List', margins: '0 0 0 3'});
                var displayPanel = Ext.create('Ext.panel.Panel', {width: 975, height: 750, layout: 'border', items: [{region: 'south', xtype: 'panel', layout: {type: 'hbox', align: 'stretch', padding: 5}, defaults: {flex: 1}, height: 450, margins: '0 5 5 5', items: [mainGrid]}, {region: 'center', xtype: 'panel', layout: {type: 'hbox', align: 'stretch', padding: 5}, defaults: {flex: 1}, margins: '5 5 0 0', items: [firstGrid, secondGrid, thirdGrid], dockedItems: {xtype: 'toolbar', dock: 'bottom', items: ['->', {text: 'Auto Refresh', handler: function () {
                                            var task = {run: function () {
                                                    var sendDataArray2 = [];
                                                    firstGridStore.each(function (record) {
                                                        var recordArray2 = [record.get("code")];
                                                        sendDataArray2.push(recordArray2);
                                                    });
                                                    var sendDataArray3 = [];
                                                    secondGridStore.each(function (record) {
                                                        var recordArray3 = [record.get("code")];
                                                        sendDataArray3.push(recordArray3);
                                                    });
                                                    var sendDataArray4 = [];
                                                    thirdGridStore.each(function (record) {
                                                        var recordArray4 = [record.get("code")];
                                                        sendDataArray4.push(recordArray4);
                                                    });
                                                    firstGridStore.removeAll();
                                                    secondGridStore.removeAll();
                                                    thirdGridStore.removeAll();
                                                    mainGridStore.proxy.extraParams = {"firstgrid": Ext.encode(sendDataArray2), "secondgrid": Ext.encode(sendDataArray3), "thirdgrid": Ext.encode(sendDataArray4)};
                                                    mainGridStore.load();
                                                    var currentDate = new Date();
                                                    mainGrid.setTitle(currentDate);
                                                }, interval: 60000};
                                            var runner = new Ext.util.TaskRunner();
                                            runner.start(task);
                                        }}, {text: 'Reset All Watch List', handler: function () {
                                            firstGridStore.removeAll();
                                            secondGridStore.removeAll();
                                            thirdGridStore.removeAll();
                                        }}, {text: 'Refresh All', handler: function () {
                                            var sendDataArray2 = [];
                                            firstGridStore.each(function (record) {
                                                var recordArray2 = [record.get("code")];
                                                sendDataArray2.push(recordArray2);
                                            });
                                            var sendDataArray3 = [];
                                            secondGridStore.each(function (record) {
                                                var recordArray3 = [record.get("code")];
                                                sendDataArray3.push(recordArray3);
                                            });
                                            var sendDataArray4 = [];
                                            thirdGridStore.each(function (record) {
                                                var recordArray4 = [record.get("code")];
                                                sendDataArray4.push(recordArray4);
                                            });
                                            mainGridStore.proxy.extraParams = {"firstgrid": Ext.encode(sendDataArray2), "secondgrid": Ext.encode(sendDataArray3), "thirdgrid": Ext.encode(sendDataArray4)};
                                            mainGridStore.load();
                                            var currentDate = new Date();
                                            mainGrid.setTitle(currentDate);
                                        }}]}}], renderTo: "gridpanel"});
            });
            if (j < 0) {
                return'<span style="color:red;">' + j + "</span>"
            }
        }
        return j
    }
    var c = Ext.create("Ext.slider.Multi", {hideLabel: true, width: 300, minValue: -11, maxValue: 11, values: [-10, 10], listeners: {change: {buffer: 70, fn: h}}});
    var e = Ext.create("Ext.slider.Multi", {hideLabel: true, width: 300, minValue: -11, maxValue: 11, values: [-10, 10], listeners: {change: {buffer: 70, fn: h}}});
    function h(k) {
        var j = k.getValues();
        var l = [];
        b.suspendEvents();
        b.clearFilter();
        b.resumeEvents();
        b.filter([{fn: function (m) {
                    return m.get("pchange") >= j[0] && m.get("pchange") <= j[1]
                }}]);
        b.sort("name", "ASC")
    }
    h(c);
    var g = Ext.create("Ext.grid.feature.Grouping", {groupHeaderTpl: '{name} ({rows.length} Item{[values.rows.length > 1 ? "s" : ""]})'});
    var d = Ext.create("Ext.grid.Panel", {width: 975, height: 600, title: "Data Matrix", renderTo: "gridpanel", id: "datamatrix", tbar: ["Filter on change %:", " ", c], store: b, viewConfig: {stripeRows: false}, features: [g, {ftype: "filters", local: true}],
        columns: [
            {header: "id", readOnly: true, dataIndex: "id", width: 70, hidden: true},
            {text: "Scrips", flex: 1, tdCls: "task", sortable: true, dataIndex: "code", hideable: false, summaryType: "count", width: 100, items: {xtype: "textfield", flex: 1, margin: 2, enableKeyEvents: true, listeners: {keyup: function () {
                            var j = this.up("tablepanel").store;
                            j.clearFilter();
                            if (this.value) {
                                j.filter({property: "code", value: this.value, anyMatch: true, caseSensitive: false})
                            }
                        }, buffer: 500}}},
            {header: "Sector", dataIndex: "sector", width: 200, hidden: true, filter: {}},
            {header: "Category", readOnly: true, dataIndex: "category", width: 50, hidden: true, filter: {}},
            {header: "LastPrice", dataIndex: "lastprice", width: 60, readOnly: true, filter: {}},
            {header: "Open", dataIndex: "open", width: 60, readOnly: true, filter: {}},
            {header: "high", dataIndex: "high", width: 60, readOnly: true, filter: {}},
            {header: "low", dataIndex: "low", width: 60, readOnly: true, filter: {}},
            {header: "ycp", dataIndex: "ycp", width: 60, readOnly: true, hidden: false, filter: {}},
            {header: "Change", renderer: i, dataIndex: "change", width: 60, readOnly: true, filter: {}},
            {header: "Change%", renderer: a, dataIndex: "pchange", width: 70, readOnly: true, filter: {}},
            {header: "(D&A) P/E", dataIndex: "pe", width: 60, readOnly: true, filter: {}},
            {header: "(D&A) EPS", dataIndex: "eps", width: 60, readOnly: true, filter: {}},
            {header: "volume", dataIndex: "volume", width: 70, readOnly: true, filter: {}},
            {header: "Value(mn)", dataIndex: "value", width: 70, readOnly: true, filter: {}},
            {header: "trade", dataIndex: "trade", width: 70, readOnly: true, filter: {}},
            {header: "Market lot", dataIndex: "market_lot", width: 60, readOnly: true, hidden: true, filter: {}},
            {header: "Face value", dataIndex: "face_value", width: 60, readOnly: true, hidden: true, filter: {}},
            {header: "NAV", dataIndex: "nav", width: 60, readOnly: true, filter: {}}],
        dockedItems: [Ext.create("Ext.toolbar.Paging", {dock: "bottom", store: b})]})
});