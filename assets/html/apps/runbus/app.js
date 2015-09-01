
Ext.application({
    name: 'Sencha',

    launch: function() {   

        var button = Ext.create('Ext.Button', {
                  text: 'Button',
                  id: 'rightButton'
        });

        var mainui = Ext.create('Ext.Container', {
            fullscreen: true,
            layout: 'vbox',
            html:"mainui",
            scrollable: {
                direction: 'vertical',
                directionLock: true
            },
            /*
            items: [
                {
                    docked: 'top',
                    xtype: 'titlebar',
                    items: [
                              button
                          ]
                },
                {
                        xtype: 'sinChatList',
                        flex: 2
                }
            ],
            */

        });
        mainui.add(button);
        mainui.add(Ext.create('Sin.ChatItem',{who:'who',msg:'this real msg'}));
        mainui.add(Ext.create('Sin.ChatItem',{who:'who',msg:'this real msg'}));
        //Ext.Viewport.add(mainui);
        //var chatlist = Ext.create('Sin.ChatListX');
        //chatlist=chatlist.createList();
        for(var i=0;i<40;i++){
            var packt=i%2!=0?'start':'end';
            var who=i%2!=0?'sin':'qq';
            mainui.add(Ext.create('Sin.ChatItem',{who:who,msg:i+'this real msg',me:i%2!=0,layout:{pack:packt}}));
        }


       // chatlist.getScrollable().getScroller().scrollToEnd();


    }
});
