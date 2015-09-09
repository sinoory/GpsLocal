
Ext.application({
    name: 'RunBus',
    //
    //controllers: ['MainControl'],
    //controllers: ['TestMain'],
    views:['LocalList'],
    launch: function() {   
        Ext.create('RunBus.view.LocalList');
        //Ext.Viewport.add({xclass:'RunBus.view.Buttons'});
        //Ext.Viewport.add({xclass:'RunBus.view.LocalList'});
        /*
        var senderroot = Ext.create('Ext.Container', {
            layout: 'hbox',
            style : "border-style: solid;border-radius: 1px",
                items: [
                    {xtype: 'textfield',name : 'name',flex:5,id:'idmsg'},
                    {xtype:'button',html:'Send',ui: 'action',flex:1,id:'idsend',},
                ],    
            });

        var mainui=Ext.create('Ext.Container', {
            fullscreen: true,layout: 'vbox',id:'idmain',
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
                }
            ]
        });
        chartroot=Ext.ComponentQuery.query("#idchat")[0];
        busroot=Ext.ComponentQuery.query("#idbus")[0];
        mainui.add(senderroot);
*/
    },
});
