Ext.define('RunBus.controller.MainControl', {
    extend: 'Ext.app.Controller',

    showHideBus: function() {
        busroot.hide=!busroot.hide;
        busroot.setHidden(busroot.hide);
        showhidebtn.setHtml(busroot.hide?'+':'-');
    },
    sendMsg:function(){
        var msg=mymsgFeid.getValue();
        if(!msg) return;
        mymsgFeid.setValue("");
        chartroot.add(Ext.create('Sin.ChatItem',{who:'sin',msg:msg}));
        chartroot.getScrollable().getScroller().scrollToEnd();
    },
    sendmsg_:function(msg){
        chartroot.add(Ext.create('Sin.ChatItem',{who:'sin',msg:msg}));
        chartroot.getScrollable().getScroller().scrollToEnd();
    },

    initEvn:function(){
        showhidebtn=Ext.ComponentQuery.query("#idshowbus")[0];
        showhidebtn.on('tap',this.showHideBus);
        sendbtn=Ext.ComponentQuery.query("#idsend")[0];
        sendbtn.on('tap',this.sendMsg);
    },

    launch: function() {   
        chartroot=Ext.ComponentQuery.query("#idchat")[0];
        busroot=Ext.ComponentQuery.query("#idbus")[0];
        busroot.hide=false;
        mainui=Ext.ComponentQuery.query("#idmain")[0];
        mymsgFeid=Ext.ComponentQuery.query("#idmsg")[0];
        runningbus=new Sin.RunningBus('#idbus');
        titlebar=Ext.ComponentQuery.query("#idbusTitle")[0];
        this.initEvn();
        this.getBusStations();
        this.initWindow();
    },

    initWindow:function(){
        window.busControl=this;
        window.dbg=function(msg){
            busControl.sendmsg_(msg);
        }
        window.onGpsPos=function(loc){
            mLatestLoc=JSON.parse(loc);
            dbg(loc);
        };
    },
    
    getBusStations:function(){
        info=window.buss.getBusInfo();
        businfo= JSON.parse(info);
        Ext.ComponentQuery.query("#idbusTitle")[0].setTitle(businfo.name);
        runningbus.setStationcnt(businfo.stations.length);
        for(var i=0;i<businfo.stations.length;i++){
            runningbus.add({name:businfo.stations[i].stname,status:'in',index:i});
        }
        if(!businfo.ownerid || businfo.ownerid==jlh.getShp("id")){
            startbtn=Ext.create('Ext.Button',{html:'Start',ui:'action',align:'right',});
            startbtn.start=false;
            startbtn.on('tap',function(){
                startbtn.start=!startbtn.start;
                startbtn.setHtml(startbtn.start?'Stop':'Start');
                if(startbtn.start){
                }
            });
            titlebar.add(startbtn);
        }else{
            watchbtn=Ext.create('Ext.Button',{html:'Watch',ui:'action',align:'right',});
            watchbtn.watch=false;
            watchbtn.on('tap',function(){
                watchbtn.watch=!watchbtn.watch;
                watchbtn.setHtml(watchbtn.watch?'Unwatch':'Watch');
            });
            titlebar.add(watchbtn);
        }

    },

});
