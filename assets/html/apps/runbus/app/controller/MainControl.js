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
        serverlinelist=Ext.ComponentQuery.query("#serverlinelist")[0];
        this.initEvn();
        sendbtn.disable();
        this.getBusStations();
        this.initWindow();
        buss.prepConn();
    },

    initWindow:function(){
        var self=this;
        window.busControl=this;
        window.dbg=function(msg){
            busControl.showLocalMsg(msg);
            //console.log("DBG "+msg);
            //Ext.Msg.alert(msg);
        };
        window.onGpsPos=function(loc){
            self.mLatestLoc=JSON.parse(loc);
            self.updateStaion();
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
        dbg("js onWsMessage type="+js.type);
        if(js.type=="re-querryid"){
            jlh.setShp("id",js.userid);
            dbg("onWsMessage id="+js.userid);
            if(js.nickname){
                jlh.setShp("nickname",js.nickname);
            }
            busControl.reportOnline(js.userid);
        }else if(js.type=='runbus'){//forward from server
            if(this.mStartBus) return ;//start by myself,so ignore server msg
            if(js.action=='start'){
                busControl.onStartLine(js);
            }else if(js.action=='stUpdate'){
                //dbg("su st:"+js.status+" ["+js.index+"] d:"+js.dist+" p:"+js.speed);
                busControl.updateServerStation(js);
            }
        }else if(js.type=='linechat'){
            busControl.onChatMsg(js);
        }else if(js.type=="re-getlines"){
            var lines=js.res;
            var linehtml="",allstations="";
            var store=serverlinelist.getStore();
            for(var i=0;i<lines.length;i++){
                allstations="";
                for(var j=0;j<lines[i].stations.length;j++){
                    allstations+=(lines[i].stations[j].stname+",");
                }
                store.add({line:lines[i].name,stations:allstations,jsline:lines[i],author:lines[i].ownerid,area:lines[i].area});
            }

        }else if(js.type=="re-uploadLine"){
            var mod =localliststore.getAt(js.index);
            var jsline=mod.get('jsline');
            if(js.subtype=="upload"){
                mod.set('author',userId);
                jsline.linechanged=false;
                jsline.ownerid=userId;
                jsline.lineid=js.lineid;
                jsline.area=js.area;
                jlh.setShp(jsline.name,JSON.stringify(jsline));
            }else{
            }

        }else{
        }

    },
    onChatMsg:function(jsmsg){
        chartroot.add(Ext.create('Sin.ChatItem',{who:jsmsg.from,msg:jsmsg.msg,me:false,layout:{pack:'start'}}));
        chartroot.getScrollable().getScroller().scrollToEnd();
    },

    clrBusInfo:function(){
        runningbus.removeAll();
    },
    getBusStations:function(){
        info=window.buss.getBusInfo();
        if(!info) return;
        businfo= JSON.parse(info);
        //titlebar.removeAll(true,true);
        Ext.ComponentQuery.query("#idbusTitle")[0].setTitle(businfo.name);
        if(titlebar.btn) titlebar.remove(titlebar.btn);
        runningbus.stationcnt=(businfo.stations.length);
        runningbus.removeAll();
        for(var i=0;i<businfo.stations.length;i++){
            runningbus.add({name:businfo.stations[i].stname,status:'next',index:i});
        }
        var self=this;
        if(!businfo.ownerid || businfo.ownerid==jlh.getShp("id")){
            startbtn=Ext.create('Ext.Button',{html:'Start',ui:'action',align:'right',});
            startbtn.start=false;
            startbtn.on('tap',function(){
                startbtn.start=!startbtn.start;
                startbtn.setHtml(startbtn.start?'Stop':'Start');
                console.log("startbtn.ontap startbtn.start="+startbtn.start);
                if(startbtn.start){
                    self.start_();
                }else{
                    self.stop_();
                }
            });
            titlebar.add(startbtn);
            titlebar.btn=startbtn;
        }else{
            watchbtn=Ext.create('Ext.Button',{html:'Watch',ui:'action',align:'right',});
            watchbtn.watch=false;
            watchbtn.on('tap',function(){
                watchbtn.watch=!watchbtn.watch;
                watchbtn.setHtml(watchbtn.watch?'Unwatch':'Watch');
            });
            titlebar.add(watchbtn);
            titlebar.btn=watchbtn
        }

    },
    toRad:function(d) {  return d * Math.PI / 180; },
    getDisance:function(lat1, lng1, lat2, lng2) { //lat为纬度, lng为经度, 一定不要弄错
        //dbg("gd ["+lat1+","+lng1+"]->["+lat2+","+lng2+"]");
        var dis = 0;
        var radLat1 = this.toRad(lat1);
        var radLat2 = this.toRad(lat2);
        var deltaLat = radLat1 - radLat2;
        var deltaLng = this.toRad(lng1) - this.toRad(lng2);
        var dis = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(deltaLat / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(deltaLng / 2), 2)));
        return parseInt(dis * 6378137);
    },

    start_:function(){
        var ls=businfo.stations;
        console.log("start_  ls.length="+ls.length+">>>");
        if(!this.mLatestLoc){
            dbg("no Gps!!");
            return;
        }
        mStartLoc=(this.mLatestLoc);
        var dist=MIN_IN_STATION_DIST;
        var closestInd=-1,closestDist=-1;
        for(var i=0;i<ls.length;i++){
            var thisdist=(this.getDisance(mStartLoc.la,mStartLoc.lo,ls[i].la,ls[i].lo));
            dbg("thisdist from "+ls[i].stname+"="+thisdist);
            if(closestDist==-1 || thisdist<closestDist){
                closestDist=thisdist;
                closestInd=i;
            }
        }
        if(closestDist<=MIN_IN_STATION_DIST){
            mCurrStationIndex=closestInd;
            mNextSationIndex=mCurrStationIndex+1;
            this.mStartBus=true;
            runningbus.startBus(closestInd);
        }else{
            this.showBusInfo(closestDist+"m too far from "+ls[closestInd].stname);
        }

        if(this.mStartBus){
            dbg("get start station "+ls[mCurrStationIndex].stname);
            this.sendBusStatus({"type":"runbus","action":"start","ownerid":userId,"lineid":businfo.lineid,"startInd":mCurrStationIndex,"stationCnt":ls.length});
            this.updateStaion();
        }else{
            this.showBusInfo("too far from any station");
        }
    },
    stop_:function(){
        this.mStartBus=false;
        runningbus.stopBus();
    },
    getHMS:function(timstap){
        var ts=new Date(timstap);
        return ts.getHours()+":"+ts.getMinutes()+":"+ts.getSeconds();
    },

    updateStaion:function(){//in station : yellow ; left staion : green , done station : red
        if(!this.mStartBus){
            return;
        }
        dbg("u: cur="+mCurrStationIndex+",next="+mNextSationIndex);
        var ls=businfo.stations,dist,nextStationIndex,speedDistIndex,isIn,thisdist="";
        var tm=new Date().getTime();
        if(mCurrStationIndex>=0){
            thisdist=(this.getDisance(this.mLatestLoc.la,this.mLatestLoc.lo,ls[mCurrStationIndex].la,ls[mCurrStationIndex].lo));
            dbg("u:c: dist from cur "+ls[mCurrStationIndex].stname+" = "+thisdist);
            if(thisdist>MIN_IN_STATION_DIST){//leave curr
                runningbus.nextStatus();
                mNextSationIndex=mCurrStationIndex+1;
                mCurrStationIndex=-1;

                thisdist=(this.getDisance(this.mLatestLoc.la,this.mLatestLoc.lo,ls[mNextSationIndex].la,ls[mNextSationIndex].lo));
                this.updateDistSpeed(mNextSationIndex,thisdist,"to");
            }else{
                this.updateDistSpeed(mCurrStationIndex,thisdist,"in");
            }
        }else if(mNextSationIndex>0 && mNextSationIndex<ls.length){
            thisdist=(this.getDisance(this.mLatestLoc.la,this.mLatestLoc.lo,ls[mNextSationIndex].la,ls[mNextSationIndex].lo));
            dbg("u:n: dist from next "+ls[mNextSationIndex].stname+" = "+thisdist+",tm="+getHMS(tm));
            isIn=thisdist<=MIN_IN_STATION_DIST;
            this.updateDistSpeed(mNextSationIndex,thisdist,isIn?"in":"to");
            if(isIn){//enter next
                mCurrStationIndex=mNextSationIndex;
                mNextSationIndex++;
                runningbus.nextStatus();
            }
        }

    },
    updateDistSpeed:function(speedDistIndex,dist,status){
        var speed= this.mLatestLoc.speed || 0;
        this.showBusInfo(dist+"m "+speed+"m/s");
        this.sendBusStatus({"type":"runbus","action":"stUpdate","lineid":businfo.lineid,"status":status,"index":speedDistIndex,"speed":speed,"dist":dist});
    },

    sendBusStatus:function(jsmsg){
        if(businfo.lineid){
            buss.sendMsg(JSON.stringify(jsmsg));
        }
    },

});
