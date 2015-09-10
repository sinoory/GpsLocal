
Ext.application({
    name: 'RunBus',
    //
    //controllers: ['MainControl'],
    controllers: ['TestMain'],
    views:['LocalList','Runningbus'],
    viewport : {
        layout : {
            type      : 'card',
            animation : 'flip'
        }
    },
    launch: function() {   
        //Ext.create('RunBus.view.Runningbus');
        //Ext.create('RunBus.view.LocalList');
        Ext.Viewport.add([
                {xtype:'tpRunningbus'},
                {xtype:'tpLocalList'},
        ]);
        /*
        setTimeout(function() {
            Ext.Viewport.setActiveItem(1);
        }, 2000);
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
        */
    },
});
