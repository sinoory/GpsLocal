
Ext.application({
    name: 'RunBus',
    controllers: ['TestMain'],
    launch: function() {   
        var senderroot = Ext.create('Ext.Container', {
            layout: 'hbox',
            style : "border-style: solid;border-radius: 1px",
                items: [
                    {xtype: 'textfield',name : 'name',flex:5},
                    {xtype:'button',html:'Send',ui: 'action',flex:1,id:'idsend',},
                ],    
            });

        var mainui=Ext.create('Ext.Container', {
            fullscreen: true,layout: 'vbox',id:'idmain',
            items: [
                {
                    xtype: 'titlebar',title:'BUS',
                    items:[
                        {xtype:'button',html:'Start',ui:'action',align:'right',
                        },
                    ],
                },
                {
                    xtype: 'container',flex:1,id:'idbus',
                    style : "border-style: solid;",
                    scrollable: {direction: 'vertical',directionLock: true},
                },
                {
                    xtype: 'titlebar',title: 'bus infomations',titleAlign:'left',
                    items:[
                        {xtype:'button',html:'-',ui:'action',align:'right',id:'idshowbus',},
                    ],
                },
                {
                    xtype: 'container',id:'idchat',flex:1,
                    style : "border-style: solid;",
                    scrollable: {direction: 'vertical',directionLock: true},
                }
            ]
        });
        chartroot=Ext.ComponentQuery.query("#idchat")[0];
        busroot=Ext.ComponentQuery.query("#idbus")[0];
        mainui.add(senderroot);

    },
});
