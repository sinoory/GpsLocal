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
        busControl.sendChatMsg(msg);
        busControl.showLocalMsg(msg);
    },
    showLocalMsg:function(msg){
        chartroot.add(Ext.create('Sin.ChatItemMe',{who:'sin',msg:msg}));
        chartroot.getScrollable().getScroller().scrollToEnd();
    },
    sendChatMsg:function(msg){
        var tm=new Date().getTime();
        var jsmsg={'type':'linechat',lineid:businfo.lineid,'from':userId,'msg':msg,'time':tm};
        buss.sendMsg(JSON.stringify(jsmsg));
    },

    initEvn:function(){
        showhidebtn=Ext.ComponentQuery.query("#idshowbus")[0];
        showhidebtn.on('tap',this.showHideBus);
        sendbtn=Ext.ComponentQuery.query("#idsend")[0];
        sendbtn.on('tap',this.sendMsg);
    },
    showBusInfo:function(info){
        Ext.ComponentQuery.query("#idbusinfo")[0].setTitle(info);
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
        sendbtn.disable();
        buss.prepConn();
    },

    initWindow:function(){
        window.busControl=this;
        window.dbg=function(msg){
            //busControl.showLocalMsg(msg);
            console.log("DBG "+msg);
            //Ext.Msg.alert(msg);
        };
        window.onGpsPos=function(loc){
            mLatestLoc=JSON.parse(loc);
        };
        dbg('onGspPos 1 setted');
        window.onWsReady=this.onWsReady;
        window.onWsMessage=this.onWsMessage;
        MIN_IN_STATION_DIST=50;
    },
    getIdIfNeed:function(){
        userId=jlh.getShp("id");
        dbg("getIdIfNeed userId="+userId);
        if(!userId || userId=="undefined"){
            var devid=buss.getDeviceId();
            dbg("no id"); 
            buss.sendMsg(JSON.stringify({type:'querryid',devid:devid}));
        }else{
            this.reportOnline(userId);
        }
    },
    reportOnline:function(userid){
        buss.sendMsg(JSON.stringify({type:'online',userid:userid}));
    },

    onStartLine:function(jsmsg){
        for(var i=0;i<jsmsg.startInd;i++){
            if(i<jsmsg.startInd)
                runningbus.setStationStatus(mCurrStationIndex,'pass');
            else if(i==jsmsg.startInd)
                runningbus.setStationStatus(mCurrStationIndex,'in');
            else
                runningbus.setStationStatus(mCurrStationIndex,'next');
        }
   
    },
    updateServerStation:function(js){
        runningbus.setStationStatus(mCurrStationIndex,js.status);
        this.showBusInfo(js.dist+"m "+js.speed+"m/s");
    },

    onWsReady:function(){
        dbg("ok onWsReady 2");
        sendbtn.enable();
        busControl.getIdIfNeed();
        dbg("ok onWsReady getid");
    },
    onWsMessage:function(msg){ //msg from websocket server
        var js=JSON.parse(msg);
        if(js.type=="re-querryid"){
            jlh.setShp("id",js.userid);
            dbg("onWsMessage id="+js.userid);
            if(js.nickname){
                jlh.setShp("nickname",js.nickname);
            }
            busControl.reportOnline(js.userid);
        }else if(js.type=='runbus'){//forward from server
            if(mStartBus) return ;//start by myself,so ignore server msg
            if(js.action=='start'){
                busControl.onStartLine(js);
            }else if(js.action=='stUpdate'){
                //dbg("su st:"+js.status+" ["+js.index+"] d:"+js.dist+" p:"+js.speed);
                busControl.updateServerStation(js);
            }
        }else if(js.type='linechat'){
            busControl.onChatMsg(js);
        }
    },
    onChatMsg:function(jsmsg){
        chartroot.add(Ext.create('Sin.ChatItem',{who:jsmsg.from,msg:jsmsg.msg,me:false,layout:{pack:'start'}}));
        chartroot.getScrollable().getScroller().scrollToEnd();
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
    getDisance:function(lat1, lng1, lat2, lng2) { //lat为纬度, lng为经度, 一定不要弄错
        //dbg("gd ["+lat1+","+lng1+"]->["+lat2+","+lng2+"]");
        var dis = 0;
        var radLat1 = toRad(lat1);
        var radLat2 = toRad(lat2);
        var deltaLat = radLat1 - radLat2;
        var deltaLng = toRad(lng1) - toRad(lng2);
        var dis = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(deltaLat / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(deltaLng / 2), 2)));
        return parseInt(dis * 6378137);
    },

    start_:function(){
        var ls=businfo.stations;
        if(!mLatestLoc){
            dbg("no Gps!!");
            return;
        }
        mStartLoc=(mLatestLoc);
        var dist=MIN_IN_STATION_DIST;
        var closestInd=-1,closestDist=-1;
        for(var i=0;i<ls.length;i++){
            var thisdist=(getDisance(mStartLoc.la,mStartLoc.lo,ls[i].la,ls[i].lo));
            dbg("thisdist from "+ls[i].stname+"="+thisdist);
            if(closestDist==-1 || thisdist<closestDist){
                closestDist=thisdist;
                closestInd=i;
            }
        }
        if(closestDist<=MIN_IN_STATION_DIST){
            mCurrStationIndex=closestInd;
            mNextSationIndex=mCurrStationIndex+1;
            mStartBus=true;
            runningbus.setStationStatus(mCurrStationIndex,'in');
        }else{
            this.showBusInfo(closestDist+"m too far from "+ls[closestInd].stname);
        }

        if(mStartBus){
            dbg("get start station "+ls[mCurrStationIndex].stname);
            for(var i=0;i<mCurrStationIndex;i++){
                runningbus.setStationStatus(i,'pass');
            }
            this.sendBusStatus({"type":"runbus","action":"start","ownerid":userId,"lineid":businfo.lineid,"startInd":mCurrStationIndex,"stationCnt":ls.length});
            updateStaion();
        }else{
            this.showBusInfo("too far from any station");
        }
    },
    getHMS:function(timstap){
        var ts=new Date(timstap);
        return ts.getHours()+":"+ts.getMinutes()+":"+ts.getSeconds();
    },

    updateStaion:function(){//in station : yellow ; left staion : green , done station : red
        if(!mStartBus){
            return;
        }
        dbg("u: cur="+mCurrStationIndex+",next="+mNextSationIndex);
        var ls=businfo.stations,dist,nextStationIndex,speedDistIndex,isIn,thisdist="";
        var tm=new Date().getTime();
        if(mCurrStationIndex>=0){
            thisdist=(this.getDisance(mLatestLoc.la,mLatestLoc.lo,ls[mCurrStationIndex].la,ls[mCurrStationIndex].lo));
            dbg("u:c: dist from cur "+ls[mCurrStationIndex].stname+" = "+thisdist);
            if(thisdist>MIN_IN_STATION_DIST){//leave curr
                runningbus.setStationStatus(mCurrStationIndex,'pass');
                mNextSationIndex=mCurrStationIndex+1;
                mCurrStationIndex=-1;

                thisdist=(this.getDisance(mLatestLoc.la,mLatestLoc.lo,ls[mNextSationIndex].la,ls[mNextSationIndex].lo));
                this.updateDistSpeed(mNextSationIndex,thisdist,"next");
            }else{
                this.updateDistSpeed(mCurrStationIndex,thisdist,"in");
            }
        }else if(mNextSationIndex>0 && mNextSationIndex<ls.length){
            thisdist=(this.getDisance(mLatestLoc.la,mLatestLoc.lo,ls[mNextSationIndex].la,ls[mNextSationIndex].lo));
            dbg("u:n: dist from next "+ls[mNextSationIndex].stname+" = "+thisdist+",tm="+getHMS(tm));
            isIn=thisdist<=MIN_IN_STATION_DIST;
            this.updateDistSpeed(mNextSationIndex,thisdist,isIn?"in":"next");
            if(isIn){//enter next
                mCurrStationIndex=mNextSationIndex;
                mNextSationIndex++;
                runningbus.setStationStatus(mCurrStationIndex,'in');
            }
        }

    },
    updateDistSpeed:function(speedDistIndex,dist,status){
        var speed= mLatestLoc.speed || 0;
        this.showBusInfo(dist+"m "+speed+"m/s");
        this.sendBusStatus({"type":"runbus","action":"stUpdate","lineid":businfo.lineid,"status":status,"index":speedDistIndex,"speed":speed,"dist":dist});
    },

    sendBusStatus:function(jsmsg){
        if(businfo.lineid){
            buss.sendMsg(JSON.stringify(jsmsg));
        }
    },

});
