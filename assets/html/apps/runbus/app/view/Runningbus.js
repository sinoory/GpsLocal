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
                layout:{
                    type:'hbox',
                },

                scrollable: {direction: 'horizontal',directionLock: true},
                //scrollable: {direction: 'vertical',directionLock: true},
            },
            {
                xtype:'label',flex:1,
                html:'label',
            },
            {
                xtype: 'container',
                id:'idinfo',
                layout:{type:'hbox',align:'center',},
                items:[
                    {xtype:'spacer'},
                    {xtype:'button',html:'查找班车',ui:'action',align:'center',iconCls: 'search',
                        handler:function(){
                            Ext.Viewport.setActiveItem(1);
                            Ext.Viewport.getActiveItem(1).onResume();
                        }
                    },
                    {xtype:'spacer'},
                    {xtype:'button',html:'创建班车',ui:'action',align:'center',iconCls:'add',},
                    {xtype:'spacer'},
                ],

            },
            {
                xtype: 'container',id:'idchat',flex:2,
                style : "border-style: solid;border-width: 3px 0px 0px 0px;",
                scrollable: {direction: 'vertical',directionLock: true},
            },
            {
                xtype: 'container',
                layout: 'hbox',
                //style : "border-style: solid;border-radius: 1px",
                items: [
                    {xtype: 'textfield',name : 'name',flex:5,id:'idmsg',
                        style : "border-style: solid;border-radius: 10px"},
                    {xtype:'button',html:'Send',ui: 'action',flex:1,id:'idsend',},
                ],    

            }


        ],
    },
});
