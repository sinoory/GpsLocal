
Ext.application({
    name: 'Sencha',

    launch: function() {   
        var senderroot = Ext.create('Ext.Container', {
            layout: 'hbox',
            style : "border-style: solid;border-radius: 1px",
                items: [
                    {xtype: 'textfield',name : 'name',flex:5},
                    {xtype:'button',html:'Send',ui: 'action',flex:1,
                        handler: function() {
                            chartroot.add(Ext.create('Sin.ChatItem',{who:'who',msg:'this real msg'}));
                        },
                    },
                ],    
            });

        var mainui=Ext.create('Ext.Container', {
            fullscreen: true,
            layout: 'vbox',
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
                        {xtype:'button',html:'-',ui:'action',align:'right',
                            handler: function() {
                                busroot.hide=!busroot.hide;
                                busroot.setHidden(busroot.hide);
                            },
                        },
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

        this.testui();
    },
    testui:function(){
        chartroot.add(Ext.create('Sin.ChatItem',{who:'who',msg:'this real msg'}));
        for(var i=0;i<10;i++){
            var packt=i%2!=0?'start':'end';
            var who=i%2!=0?'sin':'qq';
            chartroot.add(Ext.create('Sin.ChatItem',{who:who,msg:i+'this real msg',me:i%2!=0,layout:{pack:packt}}));
        }

        runningbus=new Sin.RunningBus('#idbus');
        runningbus.setStationcnt(4);
        runningbus.add({name:'company',status:'in',index:0});
        runningbus.add({name:'shop',status:'next',index:1});
        runningbus.add({name:'movie',status:'next',index:2});
        runningbus.add({name:'house',status:'next',index:3});

        runningbus.setStationStatus(2,'in');
        busroot.hide=false;

    },
});
