Ext.define('Pressure', {extend: 'Ext.data.Model', fields: ['code', 'pressure', 'volumeChange', 'hammer', 'cLengthChange', 'consecutiveGreen', 'tradeChange', 'rsi', 'divergence', 'signal', {name: 'publics', mapping: 'sharePercentage.publics'}, 'totalSecurity', 'paidUpCapital', 'signalReason', {name: 'reserve', convert: function (v, record) {
                var paidup = record.data.paidUpCapital;
                var reserveRatio = v / paidup;
                return reserveRatio.toFixed(1);
            }}, {name: 'publicShare', convert: function (v, record) {
                var totalSecurity = record.data.totalSecurity;
                var publicPercent = record.data.publics;
                var publicShare = ((totalSecurity * publicPercent) / 100) / 1000000;
                return publicShare.toFixed(1);
            }}, 'potentiality', 'dividentYield', 'bottom']});
Ext.Ajax.cors = true;
Ext.Ajax.useDefaultXhrHeader = false;
var localStore = new Ext.data.Store({
    model: 'Pressure',
    proxy: {
        type: 'ajax',
        url: 'http://localhost:8084/BSPressure/getBSPressure?sid=' + (new Date()).getTime(),
        reader: {
            type: 'json'
        }
    }
});

var gridView = Ext.ComponentQuery.query('gridpanel')[0];
//hideColumns(gridView.headerCt);
gridView.setHeight(window.innerHeight);
document.getElementById("maincontainer").style.width = "100%";
gridView.setWidth(window.innerWidth - 20);

var panels = Ext.ComponentQuery.query('panel');
Ext.each(panels, function (item) {
    var id = item.id;
    if (Ext.get(id)) {
        var parentId = Ext.get(id).parent().id;
        if (parentId === 'gridpanel')
            item.setWidth(window.innerWidth - 20);

    }
});

console.log('child size: ' + Ext.ComponentQuery.query('#gridpanel')[0]);
var store = gridView.store;
var model = store.model;
model.setFields([{name: 'id', type: 'int'}, {name: 'code', type: 'string'}, {name: 'sector', type: 'string'}, {name: 'category', type: 'string'}, {name: 'market_lot', type: 'int'}, {name: 'face_value', type: 'int'}, {name: 'nav', type: 'float'}, {name: 'lastprice', type: 'float'}, {name: 'open', type: 'float'}, {name: 'high', type: 'float'}, {name: 'low', type: 'float'}, {name: 'pchange', type: 'float'}, {name: 'change', type: 'float'}, {name: 'pe', type: 'float'}, {name: 'eps', type: 'float'}, {name: 'volume', type: 'int'}, {name: 'value', type: 'float'}, {name: 'trade', type: 'float'}, {name: 'ycp', type: 'float'}, {name: 'pressure', type: 'float', defaultValue: 0}, {name: 'volumeChange', type: 'float', defaultValue: 0}, {name: 'hammer', type: 'float', defaultValue: 0}, {name: 'cLengthChange', type: 'float', defaultValue: 0}, {name: 'consecutiveGreen', type: 'boolean', defaultValue: false}, {name: 'tradeChange', type: 'float'}, {name: 'rsi', type: 'int'}, {name: 'divergence', type: 'int'}, {name: 'signal', type: 'string', defaultValue: 'NA'}, {name: 'publics', type: 'float', defaultValue: '50'}, {name: 'publicShare', type: 'float', defaultValue: '50'}, {name: 'totalSecurity', type: 'float', defaultValue: '0'}, {name: 'paidUpCapital', type: 'float', defaultValue: '0'}, {name: 'reserve', type: 'float', defaultValue: '0'}, {name: 'potentiality', type: 'boolean', defaultValue: '0'}, {name: 'dividentYield', type: 'float', defaultValue: '0'}, {name: 'bottom', type: 'boolean', defaultValue: false}]);
store.on('load', function () {
    localStore.load(function (records, operation, success) {
        store.suspendEvents();
        for (var i in records) {
            var rec = records[i];
            var code = rec.get('code');
            var mc = store.query('code', code);
            var stockRecord = getStockRecord(mc, code);
            if (typeof stockRecord === 'undefined') {
                console.log('could not found code: ' + code);
                continue;
            }
            stockRecord.set('pressure', rec.get('pressure'));
            stockRecord.set('volumeChange', rec.get('volumeChange'));
            stockRecord.set('hammer', rec.get('hammer'));
            stockRecord.set('cLengthChange', rec.get('cLengthChange'));
            stockRecord.set('consecutiveGreen', rec.get('consecutiveGreen'));
            stockRecord.set('tradeChange', rec.get('tradeChange'));
            stockRecord.set('rsi', rec.get('rsi'));
            stockRecord.set('divergence', rec.get('divergence'));
            stockRecord.set('signal', rec.get('signal'));
            stockRecord.set('signalReason', rec.get('signalReason'));
            stockRecord.set('publics', rec.get('publics'));
            /*To calculate amount in taka */
            var publicShare = rec.get('publicShare') * store.findRecord('code', rec.get('code')).get('lastprice');
            publicShare = publicShare.toFixed(1);
//            stockRecord.set('publicShare', rec.get('publicShare'));
            stockRecord.set('publicShare', publicShare);
            stockRecord.set('totalSecurity', rec.get('totalSecurity'));
            stockRecord.set('paidUpCapital', rec.get('paidUpCapital'));
            stockRecord.set('reserve', rec.get('reserve'));
            stockRecord.set('potentiality', rec.get('potentiality'));
            stockRecord.set('dividentYield', rec.get('dividentYield'));
            stockRecord.set('bottom', rec.get('bottom'));
        }
        store.commitChanges();
        store.resumeEvents();
        var sorter = store.sorters.first();
        if (sorter)
            store.sort(sorter.property, sorter.direction);
    });
});
store.load();
var pressureColumn = Ext.create('Ext.grid.column.Column', {header: 'Pressure', dataIndex: 'pressure', width: 60, readOnly: true, filter: {}});
var volumeChangeColumn = Ext.create('Ext.grid.column.Column', {header: 'VChange', dataIndex: 'volumeChange', width: 60, readOnly: true, filter: {}});
var hammerColumn = Ext.create('Ext.grid.column.Column', {header: 'Hammer', dataIndex: 'hammer', width: 50, readOnly: true, hidden: true, filter: {}, hidden: true});
var candleChnageColumn = Ext.create('Ext.grid.column.Column', {header: 'Candle', dataIndex: 'cLengthChange', width: 50, readOnly: true, hidden: true, filter: {}, hidden: true});
var consecutiveGreen = Ext.create('Ext.grid.column.Column', {header: 'CGreen', dataIndex: 'consecutiveGreen', width: 60, readOnly: true, hidden: false, filter: {}, renderer: function (value, record) {
        if (value === true)
            value = "<span style='color:green'>" + value + "</span>";
        return value;
    }});
var tradeChangeGreen = Ext.create('Ext.grid.column.Column', {header: 'TChange', dataIndex: 'tradeChange', width: 60, readOnly: true, hidden: true, filter: {}});
var rsi = Ext.create('Ext.grid.column.Column', {header: 'RSI', dataIndex: 'rsi', width: 50, readOnly: true, filter: {}});
var divergence = Ext.create('Ext.grid.column.Column', {header: 'Divergence', dataIndex: 'divergence', width: 60, readOnly: true, filter: {}});
var signal = Ext.create('Ext.grid.column.Column', {header: 'Signal', dataIndex: 'signal', width: 60, readOnly: true, filter: {}, renderer: function (value, comp, record) {
        var reason = record.get('signalReason');
//        var reason = 'xyz';
        if (value === 'BUY')
            value = "<span style='color:green' title='"+reason+"'>" + value + "</span>";
        else if (value === 'AVG')
            value = "<span style='color:blue'>" + value + "</span>";
        else if (value === 'SELL')
            value = "<span style='color:red'>" + value + "</span>";
        return value;
    }});
var publicP = Ext.create('Ext.grid.column.Column', {header: 'publicP', dataIndex: 'publics', width: 60, readOnly: true, hidden: true, filter: {}, renderer: function (value) {
        return value.toFixed(1);
    }});
//var publicS = Ext.create('Ext.grid.column.Column', {header: 'publicS', dataIndex: 'publics', width: 60, readOnly: true, renderer:function(value, metadata, record, rowIndex, colIndex, store){
//        var totalSecurity = record.get('totalSecurity');
//        var publicPercent = record.get('publics');
//        var publicShare = ((totalSecurity*publicPercent)/100)/1000000;
//        return publicShare.toFixed(1);
//}});
var publicS = Ext.create('Ext.grid.column.Column', {header: 'publicS', dataIndex: 'publicShare', width: 60, readOnly: true, hidden: false, renderer: function (value, record) {
        if(value <= 600)
            value = "<span style='color:green'>" + value + "</span>";
        return value;
    }});
var reserve = Ext.create('Ext.grid.column.Column', {header: 'Reserve', dataIndex: 'reserve', width: 60, readOnly: true, hidden: false, renderer: function (value, record) {
        if(value >= 1)
            value = "<span style='color:green'>" + value + "</span>";
        return value;
    }});
var paidup = Ext.create('Ext.grid.column.Column', {header: 'Paidup', dataIndex: 'paidUpCapital', width: 60, readOnly: true, filter: {}, renderer: function (value, record) {
        if(value < 600)
            value = "<span style='color:green'>" + value + "</span>";
        return value;
    }});
var potentiality = Ext.create('Ext.grid.column.Column', {header: 'Sma25', dataIndex: 'potentiality', width: 60, readOnly: true, filter: {}, renderer: function (value, record) {
        if (value === true)
            value = "<span style='color:green'>" + value + "</span>";
        return value;
    }});
var dividentYield = Ext.create('Ext.grid.column.Column', {header: 'Yield', dataIndex: 'dividentYield', width: 60, readOnly: true, hidden: true});
var bottom = Ext.create('Ext.grid.column.Column', {header: 'Bottom', dataIndex: 'bottom', width: 60, readOnly: true, hidden: false, renderer: function (value, record) {
        if (value === true)
            value = "<span style='color:green'>" + value + "</span>";
        return value;
    }});

gridView.headerCt.insert(gridView.columns.length, bottom);
gridView.headerCt.insert(gridView.columns.length, dividentYield);
gridView.headerCt.insert(gridView.columns.length, potentiality);
gridView.headerCt.insert(gridView.columns.length, reserve);
gridView.headerCt.insert(gridView.columns.length, paidup);
gridView.headerCt.insert(gridView.columns.length, publicS);
gridView.headerCt.insert(gridView.columns.length, publicP);
gridView.headerCt.insert(gridView.columns.length, signal);
gridView.headerCt.insert(gridView.columns.length, pressureColumn);
gridView.headerCt.insert(gridView.columns.length, hammerColumn);
gridView.headerCt.insert(gridView.columns.length, candleChnageColumn);
gridView.headerCt.insert(gridView.columns.length, rsi);
gridView.headerCt.insert(gridView.columns.length, volumeChangeColumn);
gridView.headerCt.insert(gridView.columns.length, tradeChangeGreen);
gridView.headerCt.insert(gridView.columns.length, consecutiveGreen);
gridView.headerCt.insert(gridView.columns.length, divergence);

decorateNAVColumn(gridView.headerCt);

hideColumns(gridView.headerCt);
void(0);

function decorateNAVColumn(headerCt) {
    var navColumn = headerCt.items.getAt(18);
    navColumn.renderer = function(value, comp, record){
        if(value >= record.get('lastprice')*2)
            value = "<span style='color:green'>" + value + "</span>";
        return value;
    };
}

function hideColumns(headerCt) {
    var multislider = Ext.ComponentQuery.query('multislider')[0];
    if (multislider)
        multislider.setValue([-11, 11], false);
    gridView.store.clearGrouping();
    headerCt.items.getAt(5).hide();
    headerCt.items.getAt(6).hide();
    headerCt.items.getAt(7).hide();
    headerCt.items.getAt(8).hide();
    headerCt.items.getAt(9).hide();
//    headerCt.items.getAt(12).hide();
    headerCt.items.getAt(13).hide();
    headerCt.items.getAt(16).hide();
//    headerCt.items.getAt(18).hide();
//    headerCt.items.getAt(29).hide();
//    headerCt.items.getAt(20).hide();
//    headerCt.items.getAt(21).hide();
//    headerCt.items.getAt(28).hide();
//    headerCt.items.getAt(31).hide();
}

function getStockRecord(mc, code) {
    var stockRecord = mc.first();
    if (typeof stockRecord !== 'undefined') {
        if (stockRecord.get('code') !== code)
            stockRecord = mc.get(1);
    }
    return stockRecord;
}
