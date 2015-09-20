Ext.define('RunBus.view.ServerList',{
    xtype:'tpServerList',
    extend:'Ext.Container',
    config: {
        fullscreen: true,
        layout:'vbox', //TODO:impotant , otherwise the list can'b show
        items: [
            {
                xtype: 'titlebar',title:'Server Lines',
                items:[
                    {xtype:'button',html:'<',ui:'action',align:'right',
                        handler:function(){Ext.Viewport.setActiveItem(1);}
                    },
                ],

            },
            {
                xtype: 'toolbar',
                items: [
                    {xtype: 'searchfield',placeHolder: 'Search',name: 'searchfield',flex:2,
                        listeners: {
                            keyup: function(who,e){
                                if(e.browserEvent.which==229){
                                    Ext.Msg.alert("keyup event e="+e.browserEvent.which);
                                }
                            },
                        },
                    },
                    {
                        xtype: 'selectfield',flex:2,
                        name : 'options',
                        //label: Ext.theme.name === "Blackberry" ? 'Select': 'select',
                        options: [
                            {text: 'by line name',  value: '1'},
                            {text: 'by author', value: '2'},
                            {text: 'by position', value: '3'}
                        ]
                    },
                    //{xtype:'button',html:'Search',ui:'action',flex:1},

                ]
            },

            {
                xtype: 'list',
                flex:1,id: 'serverlinelist', //must add flex:1 , other wise list will not show
                itemTpl: '<b>{line}</b><br> {stations}',
                infinite: true,
                useSimpleItems: true,
                variableHeights: true,
                striped: true,
                store: {
                    fields: ['line', 'stations','jsline'],
                    sorters: 'line',
                    data: [
                        //{ line: 'Greg',    stations: 'Barry' },
                    ],

                },

                onItemDisclosure: function(record, item, index, e) {
                     e.stopEvent();
                     //Ext.Msg.alert('Disclose', 'index='+index+",line=" + record.get('line'));
                     if(!this.action){
                         this.action=Ext.Viewport.add(
                            {
                                xtype: 'actionsheet',
                                centered:true,
                                items: [
                                    {text: 'Delete',ui  : 'decline',
                                        handler : function(){
                                        },
                                    },
                                    {text: 'Download',
                                        handler : function(){

                                            var l=tSvRc.get("jsline");
                                            var rcd={line:tSvRc.get('line'),stations:tSvRc.get('stations'),author:l.ownerid,area:l.area,index:locallines.length,shortarea:l.shortarea,jsline:l};
                                            console.log("download:"+JSON.stringify(rcd));

                                            localliststore.add(rcd);
                                            jlh.setShp(tSvRc.get('line'),JSON.stringify(tSvRc.jsline));
                                            locallines.push(tSvRc.get('line'));
                                            jlh.setShp("alllines",locallines.join(","));
                                            this.getParent().hide();

                                        },
                                    },
                                    {text: 'Cancel',ui  : 'confirm',
                                        handler : function(){
                                            this.getParent().hide();
                                        },
                                    }
                                ]
                            });
                     }
                     tSvRc=record;
                     var auth=record.get('author');
                     console.log("DBG ServerList auth="+auth+",userId="+userId);
                     if((auth==userId ) ){ this.action.getAt(0).show();}
                     else this.action.getAt(0).hide();

                     this.action.show();
                 },
                listeners: {
                    select: function(view, record) {
                        //Ext.Msg.alert('Selected!', 'You selected ' + record.get('line'));
                    }
                },
            },
        ],
        listeners: {
                activeitemchange: 'onActiveItemChange',
                //itemindexchange: 'onItemIndexChange'
        },

    },

    onActiveItemChange: function(carousel, newItem, oldItem) {
        Ext.Msg.alert("LocalList onActiveItemChange:");
    },

    onResume:function(){
        var store=serverlinelist.getStore();
        store.removeAll();
        buss.sendMsg(JSON.stringify({"type":"getlines"}));
    },
        //var listConfiguration = this.getListConfiguration();
        //Ext.Viewport.add(listConfiguration);
        /*
        list=Ext.ComponentQuery.query("#list")[0];
        store=list.getStore();
        //store.getAt(1).set('line','王');
        store.add({line:'house',stations:'aa,bb,cc'});
        store.add({line:'house',stations:'aa,bb,cc'});
        //注册滚动的监听事件  用于分页
        list.getScrollable().getScroller().on('scrollend', function(scroller, offset) {
                   console.log(offset.y); 
                   console.log(scroller.maxPosition.y);
                   if(offset.y >= scroller.maxPosition.y){
                        //加载下一页数据
                        list.getStore().loadData([
                        { line: 'fwt', stations: '' }
                        ],true);
                   } 
        });
        */

});
