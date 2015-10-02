
Ext.application({
    name: 'RunBus',
    //
    controllers: ['MainControl'],
    //controllers: ['TestMain'],
    views:['LocalList','ServerList','Runningbus','Testview'],
    /*
    viewport : {
        layout : {
            type      : 'card',
            animation : 'flip'
        }
    },
    */
    launch: function() {   
        //Ext.create('RunBus.view.Runningbus');
        //Ext.create('RunBus.view.LocalList');
        /*
        Ext.Viewport.add([
                {xtype:'tpRunningbus'},
                {xtype:'tpLocalList'},
                {xtype:'tpServerList'},
                {xtype:'testview'},
        ]);
        */
        /*
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

        //Ext.create('Ext.TabPanel', {
        Ext.create('Ext.tab.Panel', {
            fullscreen: true,
            tabBarPosition: 'bottom',

            defaults: {
                styleHtmlContent: true
            },

            items: [
                {xtype:'tpRunningbus'},
                {xtype:'tpLocalList'},
                {xtype:'tpServerList'},
            ]
        });
    }
});
