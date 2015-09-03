Ext.define('RunBus.controller.TestMain', {
    extend: 'Ext.app.Controller',

    config: {
        refs: {
            loginForm: 'formpanel'
        },
        control: {
            'formpanel button': {
                tap: 'doLogin'
            }
        }
    },

    doLogin: function() {
        var form   = this.getLoginForm(),
            values = form.getValues();

        MyApp.authenticate(values);
    },

    launch: function() {   
        chartroot=Ext.ComponentQuery.query("#idchat")[0];
        busroot=Ext.ComponentQuery.query("#idbus")[0];
        mainui=Ext.ComponentQuery.query("#idmain")[0];
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
