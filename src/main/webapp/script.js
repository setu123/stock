Ext.define('Pressure', {extend: 'Ext.data.Model', fields: ['code', 'pressure', 'volumeChange', 'hammer', 'cLengthChange', 'consecutiveGreen', 'tradeChange', 'rsi', 'divergence', 'signal', 'vtcSignal']});
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
hideColumns(gridView.headerCt);
var store = gridView.store;
var model = store.model;
model.setFields([{name: 'id', type: 'int'}, {name: 'code', type: 'string'}, {name: 'sector', type: 'string'}, {name: 'category', type: 'string'}, {name: 'market_lot', type: 'int'}, {name: 'face_value', type: 'int'}, {name: 'nav', type: 'float'}, {name: 'lastprice', type: 'float'}, {name: 'open', type: 'float'}, {name: 'high', type: 'float'}, {name: 'low', type: 'float'}, {name: 'pchange', type: 'float'}, {name: 'change', type: 'float'}, {name: 'pe', type: 'float'}, {name: 'eps', type: 'float'}, {name: 'volume', type: 'int'}, {name: 'value', type: 'float'}, {name: 'trade', type: 'float'}, {name: 'ycp', type: 'float'}, {name: 'pressure', type: 'float', defaultValue: 0}, {name: 'volumeChange', type: 'float', defaultValue: 0}, {name: 'hammer', type: 'float', defaultValue: 0}, {name: 'cLengthChange', type: 'float', defaultValue: 0}, {name: 'consecutiveGreen', type: 'boolean', defaultValue: false}, {name: 'tradeChange', type: 'float'}, {name: 'rsi', type: 'int'}, {name: 'divergence', type: 'int'}, {name: 'signal', type: 'string', defaultValue: 'NA'}, {name: 'vtcSignal', type: 'string', defaultValue: 'HOLD'}]);
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
            stockRecord.set('vtcSignal', rec.get('vtcSignal'));
        }
        store.commitChanges();
        store.resumeEvents();
        var sorter = store.sorters.first();
        store.sort(sorter.property, sorter.direction);
    });
});
store.load();
var pressureColumn = Ext.create('Ext.grid.column.Column', {header: 'Pressure', dataIndex: 'pressure', width: 60, readOnly: true, filter: {}});
var volumeChangeColumn = Ext.create('Ext.grid.column.Column', {header: 'VChange', dataIndex: 'volumeChange', width: 60, readOnly: true, filter: {}});
var hammerColumn = Ext.create('Ext.grid.column.Column', {header: 'Hammer', dataIndex: 'hammer', width: 50, readOnly: true, filter: {}, hidden: true});
var candleChnageColumn = Ext.create('Ext.grid.column.Column', {header: 'Candle', dataIndex: 'cLengthChange', width: 50, readOnly: true, filter: {}, hidden: true});
var consecutiveGreen = Ext.create('Ext.grid.column.Column', {header: 'CGreen', dataIndex: 'consecutiveGreen', width: 60, readOnly: true, filter: {}});
var tradeChangeGreen = Ext.create('Ext.grid.column.Column', {header: 'TChange', dataIndex: 'tradeChange', width: 60, readOnly: true, filter: {}});
var rsi = Ext.create('Ext.grid.column.Column', {header: 'RSI', dataIndex: 'rsi', width: 50, readOnly: true, filter: {}});
var divergence = Ext.create('Ext.grid.column.Column', {header: 'Divergence', dataIndex: 'divergence', width: 60, readOnly: true, filter: {}});
var signal = Ext.create('Ext.grid.column.Column', {header: 'Signal', dataIndex: 'signal', width: 60, readOnly: true, filter: {}});
var vtcSignal = Ext.create('Ext.grid.column.Column', {header: 'VTC', dataIndex: 'vtcSignal', width: 60, readOnly: true, filter: {}});
gridView.headerCt.insert(gridView.columns.length, vtcSignal);
gridView.headerCt.insert(gridView.columns.length, signal);
gridView.headerCt.insert(gridView.columns.length, pressureColumn);
gridView.headerCt.insert(gridView.columns.length, hammerColumn);
gridView.headerCt.insert(gridView.columns.length, candleChnageColumn);
gridView.headerCt.insert(gridView.columns.length, rsi);
gridView.headerCt.insert(gridView.columns.length, volumeChangeColumn);
gridView.headerCt.insert(gridView.columns.length, tradeChangeGreen);
gridView.headerCt.insert(gridView.columns.length, consecutiveGreen);
gridView.headerCt.insert(gridView.columns.length, divergence);
void(0);

function hideColumns(headerCt) {
    gridView.store.clearGrouping();
    headerCt.items.getAt(5).hide();
    headerCt.items.getAt(6).hide();
    headerCt.items.getAt(7).hide();
    headerCt.items.getAt(8).hide();
    headerCt.items.getAt(12).hide();
    headerCt.items.getAt(13).hide();
    headerCt.items.getAt(16).hide();
    headerCt.items.getAt(18).hide();
}

function getStockRecord(mc, code) {
    var stockRecord = mc.first();
    if (typeof stockRecord !== 'undefined') {
        if (stockRecord.get('code') !== code)
            stockRecord = mc.get(1);
    }
    return stockRecord;
}