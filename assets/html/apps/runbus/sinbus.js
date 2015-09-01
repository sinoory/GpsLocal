Ext.define('Sin.BusStation',{
    extend:'Ext.Container',
    config:{
        name:'name',
        status:'next',//pass,in,next
        layout:{
            type:'hbox',
        },
    },
    constructor:function(){
        this.callParent(arguments);
        this.add(Ext.create('Ext.Label',{html:this.getName()}));
        if(this.getStatus()=='next'){
            this.element.setStyle("background-color","blue");
        }else if(this.getStatus()=='in'){
            this.element.setStyle("background-color","yellow");
        }else{
            this.element.setStyle("background-color","red");
        }
    }
});


Ext.define('Sin.RunningBus', {
    mainid:'aaaa',
    constructor:function(mainid){
        this.main=Ext.ComponentQuery.query(mainid)[0];
        if(!this.main){
            alert("Err , "+mainid+" not found");
        }
    },
    add:function(station){
        this.main.add(Ext.create('Sin.BusStation',station));
    },
});


