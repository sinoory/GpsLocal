
Ext.application({
    name: 'RunBus',
    //
    //controllers: ['MainControl'],
    controllers: ['TestMain'],
    views:['LocalList','Runningbus'],
    launch: function() {   
        //Ext.create('RunBus.view.LocalList');
        Ext.create('RunBus.view.Runningbus');
    },
});
