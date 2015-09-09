Ext.define('RunBus.view.Runningbus',{
    xtype:'tpRunningbus',
    extend:'Ext.Container',
    config: {
        fullscreen: true,
        layout:'vbox', //TODO:impotant , otherwise the list can'b show
        items: [
            {
                xtype: 'titlebar',title:'BUS',id:'idbusTitle',
            },
            {
                xtype: 'container',flex:1,id:'idbus',
                style : "border-style: solid;",
                scrollable: {direction: 'vertical',directionLock: true},
            },
            {
                xtype: 'titlebar',title: 'bus infomations',titleAlign:'left',id:'idbusinfo',
                items:[
                    {xtype:'button',html:'-',ui:'action',align:'right',id:'idshowbus',},
                ],
            },
            {
                xtype: 'container',id:'idchat',flex:1,
                style : "border-style: solid;",
                scrollable: {direction: 'vertical',directionLock: true},
            },
            {
                xtype: 'container',
                layout: 'hbox',
                style : "border-style: solid;border-radius: 1px",
                items: [
                    {xtype: 'textfield',name : 'name',flex:5,id:'idmsg'},
                    {xtype:'button',html:'Send',ui: 'action',flex:1,id:'idsend',},
                ],    

            }


        ],
    },
});
