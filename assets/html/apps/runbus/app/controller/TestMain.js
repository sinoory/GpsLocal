Ext.define('RunBus.controller.TestMain', {
    extend: 'RunBus.controller.MainControl',


    launch: function() {   
        chartroot=Ext.ComponentQuery.query("#idchat")[0];
        busroot=Ext.ComponentQuery.query("#idbus")[0];
        mainui=Ext.ComponentQuery.query("#idmain")[0];
        mymsgFeid=Ext.ComponentQuery.query("#idmsg")[0];
        runningbus=new Sin.RunningBus('#idbus');
        this.initEvn();
        this.testui();

        titlebar=Ext.ComponentQuery.query("#idbusTitle")[0];
        watchbtn=Ext.create('Ext.Button',{html:'Watch',ui:'action',align:'right',});
        titlebar.add(watchbtn);
        watchbtn.setHtml("unwatch");
        var self=this;
        watchbtn.on('tap',function(){
            self.testRuning();
            //Ext.Viewport.setActiveItem(1);
            //runningbus.removeAll();
            //chartroot.removeAll(true,true);
            //titlebar.removeAll(true,true);
        });
        this.initWindow();
        window.testmain=this;

    },

    testRuning:function(){
        console.log("testRuning "+this.busStarted);
        if(this.busStarted){
            console.log("testRuning res="+runningbus.nextStatus());
        }else{
            this.busStarted=true;
            runningbus.startBus(1);
        }
    },
    

    initWindow:function(){
        window.busControl=this;
        window.onGpsPos=function(loc){
            console.log("onGpsPos loc="+loc);
            busControl.sendmsg_(loc);
        };
    },


    showGuild:function(){
    },

    testui:function(){
        chartroot.add(Ext.create('Sin.ChatItem',{who:'who',msg:'this real msg'}));
        for(var i=0;i<10;i++){
            var who=i%2!=0?'sin':'qq';
            var type=i%2!=0?'Sin.ChatItemMe':'Sin.ChatItemOther';
            chartroot.add(Ext.create(type,{who:who,msg:i+'this real msg'}));
        }

        runningbus=new Sin.RunningBus('#idbus');
        runningbus.stationcnt=(4);
        runningbus.add({name:'company',status:'next',index:0});
        runningbus.add({name:'shop',status:'next',index:1});
        runningbus.add({name:'movie',status:'next',index:2});
        runningbus.add({name:'house',status:'next',index:3});

        //runningbus.setStationStatus(1,'in');
        busroot.hide=false;

    },

});
