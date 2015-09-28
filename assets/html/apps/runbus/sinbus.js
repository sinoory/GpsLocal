Ext.define('Sin.BusStation',{
    extend:'Ext.Container',
    config:{
        name:'name',
        status:'next',//pass,in,reaching,next
        index:0,
        flashColor:'yellow',
        layout:{
            type:'hbox',
        },
    },
    constructor:function(index){
        this.callParent(arguments);
        this.indictor=Ext.create('Ext.Container',{html:"<canvas id='canv"+this.getIndex()+"' width='20' height='20'></canvas>"});
        this.add(this.indictor);
        this.stlable=Ext.create('Ext.Container',{html:"<b>"+this.getName()+"</b>&nbsp;<span id='idEntTime'"+index+"></span>",maxHeight:'20px',align:'right'});
        this.add(this.stlable);
        /*
        if(this.getStatus()=='next'){
            this.stlable.element.setStyle("background-color","blue");
        }else if(this.getStatus()=='in'){
            this.stlable.element.setStyle("background-color","yellow");
        }else{
            this.stlable.element.setStyle("background-color","red");
        }
        */
    },
    initStatus:function(){
        var context = this.ctx;
        var cw = this.canvas.width;
        var ch = this.canvas.height;

        console.log(cw,ch);
        context.lineWidth = 2;
        context.strokeStyle = 'black';

        context.moveTo(cw/2,0);
        context.lineTo(cw/2,ch);
        context.stroke();

        context.fillStyle="blue";
        context.beginPath();
        context.arc(cw/2,ch/2,5,0,Math.PI*2,true);
        context.closePath();
        context.fill();
    },
    getHMS:function(timstap){
        var ts=new Date(timstap);
        return ts.getHours()+":"+ts.getMinutes()+":"+ts.getSeconds();
    },


    setBusStatus:function(status){
        console.log("setBusStatus from "+this.getStatus()+" to "+status);
        if(status==this.getStatus()){return}
        var context = this.ctx;
        var cw = this.canvas.width;
        var ch = this.canvas.height;

        this.setStatus(status);
        if(status=='in'){
            this.setFlashColor('yellow');
            var s=this;
            this.flashtimer=setInterval(function(){s.flashCircle();},500);
            this.enterTime=new Date().getTime();
        }else if(status=='to'){
            this.setFlashColor('Cyan');
            var s=this;
            this.flashtimer=setInterval(function(){s.flashCircle();},1000);
        }else if(status=='next'){
            this.drawCircle("blue");
        }else{
            this.drawCircle("red");
        }
    },
    drawCircle:function (color){
        var context = this.ctx;
        var cw = this.canvas.width;
        var ch = this.canvas.height;
        context.fillStyle=color;
        context.beginPath();
        context.arc(cw/2,ch/2,5,0,Math.PI*2,true);
        context.closePath();
        context.fill();
    },
    flashCircle:function(){
        //console.log("in flashCircle color="+this.getFlashColor()+", status="+this.getStatus());
        if(this.getStatus()=='in'){
            this.drawCircle(this.getFlashColor());
            if(this.getFlashColor()=='yellow'){
                this.setFlashColor('DarkGoldenRod');
            }else{
                this.setFlashColor('yellow');
            }
        }else if(this.getStatus()=='to'){
            this.drawCircle(this.getFlashColor());
            if(this.getFlashColor()=='Cyan'){
                this.setFlashColor('DarkCyan');
            }else{
                this.setFlashColor('Cyan');
            }
        }else if(this.getStatus()=='next'){
            this.drawCircle("blue");
            clearInterval(this.flashtimer);
        }else{
            this.drawCircle("red");
            clearInterval(this.flashtimer);
        }
    },
    stopFlash:function(){
        clearInterval(this.flashtimer);
    },


});
Ext.define('Sin.BusStartStation',{
    extend:'Sin.BusStation',
    initStatus:function(){
        var context = this.ctx;
        var cw = this.canvas.width;
        var ch = this.canvas.height;

        context.lineWidth = 2;
        context.strokeStyle = 'black';

        context.moveTo(cw/2,cw/2);
        context.lineTo(cw/2,ch);
        context.stroke();

        this.drawCircle("blue");
    },

});

Ext.define('Sin.BusEndStation',{
    extend:'Sin.BusStation',
    initStatus:function(){
        var context = this.ctx;
        var cw = this.canvas.width;
        var ch = this.canvas.height;

        console.log(cw,ch);
        context.lineWidth = 2;
        context.strokeStyle = 'black';

        context.moveTo(cw/2,0);
        context.lineTo(cw/2,ch/2);
        context.stroke();

        this.drawCircle("blue");
    },

});


Ext.define('Sin.RunningBus', {
    constructor:function(mainid){
        this.currentStatus='next';
        this.currentIndex=0;
        this.stationCnt=0;
        this.test='testval';

        this.main=Ext.ComponentQuery.query(mainid)[0];
        if(!this.main){
            alert("Err , "+mainid+" not found");
        }
        this.stations=new Array();
    },
    add:function(station){
        var type=(station.index==0)?'Sin.BusStartStation':
                (station.index==this.stationcnt-1?'Sin.BusEndStation':'Sin.BusStation');
        var s=Ext.create(type,station);
        this.main.add(s);
        this.stations.push(s);
        s.canvas = document.getElementById('canv'+station.index);
        s.ctx=s.canvas.getContext("2d");
        s.initStatus();
    },

    //for watcher
    setStationStatus:function(index,status){
        console.log("setStationStatus ["+this.currentIndex+"]="+this.currentStatus+",test="+this.test);

        this.stations[this.currentIndex].stopFlash();
        for(var j=this.currentIndex;j<index;j++){
            this.stations[j].setBusStatus('pass');
        }

        this.stations[index].setBusStatus(status);
        this.currentStatus=status;
        this.currentIndex=(index);

    },

    //for driver
    startBus:function(index){
        console.log("startBus from "+index+", total="+this.stationcnt);
        for(var j=0;j<this.stationcnt;j++){
            var st=j<index?'pass':(j>index?'next':'in')
            this.stations[j].setBusStatus(st);
        }
        this.currentIndex=(index);
        this.currentStatus=('in');
    },
    stopBus:function(){
        this.stations[this.currentIndex].stopFlash();
        for(var j=0;j<this.stationcnt;j++){
            this.stations[j].setBusStatus('next');
        }

    },
    nextStatus:function(){
        if(this.currentStatus=='in'){
            this.stations[this.currentIndex].stopFlash();
            this.stations[this.currentIndex].setBusStatus('pass');
            if(this.currentIndex==this.stationcnt-1){
                return "finished"
            }
            this.stations[++this.currentIndex].setBusStatus('to');
            this.currentStatus=('to');
        }else if(this.currentStatus=='to'){
            this.stations[this.currentIndex].stopFlash();
            this.stations[this.currentIndex].setBusStatus('in');
            this.currentStatus=('in');
        }else{
            return "unreachable";
            console.log("XXXX nextStatus this.currentIndex="+this.currentIndex+",this.currentStatus="+this.currentStatus);
        }
        return "running";

    },
    setEnterTime:function(index,time){
        Ext.ComponentQuery.query("#idEntTime"+index)[0].setHtml(time);
    },
    removeAll:function(){
        this.main.removeAll(true,true);
    },
});


