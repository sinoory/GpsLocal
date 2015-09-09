
Ext.application({
    name: 'RunBus',
    //
    //controllers: ['MainControl'],
    controllers: ['TestMain'],
    views:['LocalList','Runningbus'],
    launch: function() {   
        //Ext.create('RunBus.view.Runningbus');
        //Ext.create('RunBus.view.LocalList');
        MainUi=new Ext.Panel({
            fullscreen: true,
            layout: 'card',
            cardAnimation: 'slide',
            //animateActiveItem
            items: [    
                {xtype:'tpLocalList'},
                {xtype:'tpRunningbus'},
            ]
        });
        MainUi.setActiveItem(1);
    },
});
