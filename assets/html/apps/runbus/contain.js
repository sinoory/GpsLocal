
Ext.application({
    name: 'Sencha',

    launch: function() {   
        var aboutPanel = Ext.create('Ext.Container', {
            layout: 'hbox',
            style : "border-style: solid;border-radius: 1px",
                items: [
                    {xtype: 'textfield',name : 'name',flex:5},
                    {xtype:'button',html:'Send',ui: 'action',flex:1,
                        handler: function() {
                            main.add(Ext.create('Sin.ChatItem',{who:'who',msg:'this real msg'}));
                            //main.scrollToEnd();
                        },
                    },
                ],    
            });

        var mainui=Ext.create('Ext.Container', {
            fullscreen: true,
            layout: 'vbox',
            items: [
                {
                    xtype: 'titlebar',
                    title:'BUS',
                    items:[
                        {xtype:'button',html:'Start',ui:'action',align:'right',
                        },
                    ],
                },
                {
                    xtype: 'container',
                    style : "border-style: solid;border-radius: 1px",
                    flex:1,
                    id:'idbus',
                    scrollable: {direction: 'vertical',directionLock: true},
                },
                {
                    xtype: 'container',
                    flex:1,
                    style : "border-style: solid;border-radius: 1px",
                    id:'idchat',
                    scrollable: {direction: 'vertical',directionLock: true},
                }
            ]
        });
        var main=Ext.ComponentQuery.query("#idchat")[0];
        main.add(Ext.create('Sin.ChatItem',{who:'who',msg:'this real msg'}));
        for(var i=0;i<10;i++){
            var packt=i%2!=0?'start':'end';
            var who=i%2!=0?'sin':'qq';
            main.add(Ext.create('Sin.ChatItem',{who:who,msg:i+'this real msg',me:i%2!=0,layout:{pack:packt}}));
        }

        runningbus=new Sin.RunningBus('#idbus');
        runningbus.add({name:'company',status:'in'});
        runningbus.add({name:'house',status:'next'});

        mainui.add(aboutPanel);
    }
});
