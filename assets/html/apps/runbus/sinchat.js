Ext.define('Sin.ChatBabble',{
    extend: 'Ext.Label',
    xtype:'sinChatBabble',
    config:{
        style : "background-color:#9393FF;border-style: solid;border-radius: 5px",//work
        html: 'My label kk!ooooooo<br>sinChatBabble',
        border:"2px",
        backcolor:'#28FF28',
    },
    initialize:function(){
        this.callParent(arguments);
        this.element.on('tap',this.onTap,this);
        this.element.setStyle("background-color",this.getBackcolor());
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
        layout:{
            type:'hbox',
            pack:'end',
        },
    },
    constructor:function(){
        this.callParent(arguments);
    }
});

Ext.define('Sin.ChatItemMe',{
    extend:'Sin.ChatItem',
    config:{
        backcolor:'#9393FF',
    },
    constructor:function(){
        this.callParent(arguments);
        var img = Ext.create('Ext.Img', {src:'imgs/user.png',width:20,height:20});
        this.add(Ext.create('Sin.ChatBabble',{html:this.getMsg(),backcolor:this.getBackcolor()}));
        this.add(Ext.create('Ext.Label',{html:this.getWho()}));
        this.add(img);

    }

});

Ext.define('Sin.ChatItemOther',{
    extend:'Sin.ChatItem',
    config:{
        msg:'hello,msg from other',
        who:'other',
        backcolor:'#28FF28',
        layout:{
            type:'hbox',
            pack:'start',
        },
    },
    constructor:function(){
        this.callParent(arguments);
        var img = Ext.create('Ext.Img', {src:'imgs/user.png',width:20,height:20});
        this.add(img);
        this.add(Ext.create('Ext.Label',{html:this.getWho()}));
        this.add(Ext.create('Sin.ChatBabble',{html:this.getMsg(),backcolor:this.getBackcolor()}));

    },
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



