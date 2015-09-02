Ext.define('Sin.BusStation',{
    extend:'Ext.Container',
    config:{
        name:'name',
        status:'next',//pass,in,next
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
        this.stlable=Ext.create('Ext.Container',{html:"<b>"+this.getName()+"</b>&nbsp;<span></span>",maxHeight:'20px',align:'right'});
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
        console.log("in flashCircle color="+this.getFlashColor()+", status="+this.getStatus());
        if(this.getStatus()=='in'){
            this.drawCircle(this.getFlashColor());
            if(this.getFlashColor()=='yellow'){
                console.log("flashColor change color to blue");
                this.setFlashColor('blue');
            }else{
                this.setFlashColor('yellow');
            }
        }else if(this.getStatus()=='next'){
            this.drawCircle("blue");
            clearInterval(this.flashtimer);
        }else{
            this.drawCircle("red");
            clearInterval(this.flashtimer);
        }
    }


});
Ext.define('Sin.BusStartStation',{
    extend:'Sin.BusStation',
    initStatus:function(){
        var context = this.ctx;
        var cw = this.canvas.width;
        var ch = this.canvas.height;

        console.log(cw,ch);
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
    config:{
        stationcnt:0,
        runningStation:-1,
        runningstatus:'next',//leave,in,next
    },
    constructor:function(mainid){
        this.main=Ext.ComponentQuery.query(mainid)[0];
        if(!this.main){
            alert("Err , "+mainid+" not found");
        }
        this.stations=new Array();
    },
    add:function(station){
        var type=(station.index==0)?'Sin.BusStartStation':
                (station.index==this.getStationcnt()-1?'Sin.BusEndStation':'Sin.BusStation');
        console.log('type='+type+',this.stationcnt='+this.stationcnt+","+this.getStationcnt());
        var s=Ext.create(type,station);
        this.main.add(s);
        this.stations.push(s);
        s.canvas = document.getElementById('canv'+station.index);
        s.ctx=s.canvas.getContext("2d");
        s.initStatus();
    },
    setStationStatus:function(index,status){
        this.stations[index].setBusStatus(status);
    },
});


