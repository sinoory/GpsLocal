Ext.define('Sin.ChatBabble',{
    extend: 'Ext.Label',
    xtype:'sinChatBabble',
    config:{
        //right : "10%", //work
        //left : "2%", //work
        style : "background-color:yellow;border-style: solid;border-radius: 5px",//work
        html: 'My label kk!ooooooo<br>sinChatBabble',
        border:"2px",
        me:false, //true if this message is send from local , else from other people
        //styleHtmlContent:false,//work
    },
    initialize:function(){
        this.callParent(arguments);
        this.element.on('tap',this.onTap,this);
        if(this.getMe()){
            this.element.setStyle("background-color","red");
        }
    },
    onTap:function(){
        Ext.Msg.alert("hello");
    },
    

});

Ext.define('Sin.ChatItem',{
    extend:'Ext.Container',
    config:{
        msg:'hello,this is msg',
        who:'sin',
        me:false,
        layout:{
            type:'hbox',
            pack:'end',
        },
    },
    constructor:function(){
        this.callParent(arguments);
        if(this.getMe()){
            this.add(Ext.create('Ext.Label',{html:this.getWho()}));
            this.add(Ext.create('Sin.ChatBabble',{html:this.getMsg(),me:true}));
        }else{
            this.add(Ext.create('Sin.ChatBabble',{html:this.getMsg()}));
            this.add(Ext.create('Ext.Label',{html:this.getWho()}));
        }
    }
});


Ext.define('Sin.ChatListX', {
    extend:'Ext.Container',
    xtype:'sinChatList',
    config:{
            fullscreen: true,
            layout: 'vbox',
            html:'chatlistX',
            scrollable: {
                direction: 'vertical',
                directionLock: true
            }
    }
});



