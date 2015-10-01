Ext.define('RunBus.view.LocalList',{
    xtype:'tpLocalList',
    extend:'Ext.Container',
        config: {
        //fullscreen: true,
        layout:'vbox', //TODO:impotant , otherwise the list can'b show
        title: '班车', //if want add to tabPanel,must add title & iconCls & not fullscrren
        iconCls: 'time',
        items: [
            {
                xtype: 'titlebar',title:'Local Lines',
                items:[
                    {xtype:'button',html:'<',ui:'action',align:'right',
                        handler:function(){Ext.Viewport.setActiveItem(0);}
                    },
                    {xtype:'button',html:'>',ui:'action',align:'right',
                        handler:function(){
                            Ext.Viewport.setActiveItem(2);
                            Ext.Viewport.getActiveItem(2).onResume();
                        }
                    },
                ],

            },
            {
                xtype: 'list',
                flex:1,id: 'idlocallist', //must add flex:1 , other wise list will not show
                itemTpl: '<b>{author} {area} {line}</b><br> {stations}',
                store: {
                    fields: ['line', 'stations','author','area','index','jsline'],
                    //sorters: 'line',
                    data: [
                    //{'line':'line','stations':'aa,bb,cc','author':'author'},
                        ],
                },

                infinite: true,
                useSimpleItems: true,
                variableHeights: true,
                striped: true,
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
                                            localliststore.remove(tLcRcd);
                                            jlh.rmShp(tLcRcd.get('line'));
                                            locallines.splice(tLcRcd.get('index'),1);
                                            jlh.setShp("alllines",locallines.join(","));
                                            jlh.setShp("lastLine","");
                                            busControl.getBusStations();
                                            this.getParent().hide();
                                        },
                                    },
                                    {text: 'Select',
                                        handler : function(){
                                            jlh.setShp("lastLine",tLcRcd.get('line'));
                                            busControl.getBusStations();
                                            this.getParent().hide();
                                        },
                                    },
                                    {text: 'Upload',
                                        handler : function(){
                                            var jsline=tLcRcd.get('jsline');
                                            var js={"type":"uploadLine","stations":jsline.stations,
                                                "name":jsline.name,"ownerid":userId,"lver":jsline.lver,lineid:jsline.lineid,index:tLcRcd.lsIndex,area:tLcRcd.get('area')};
                                            jlh.sendMsg(JSON.stringify(js));
                                            this.getParent().hide();
                                        },
                                    },
                                    {text: 'SetArea',ui  : 'confirm',
                                        handler : function(){
                                            this.getParent().hide();
                                            Ext.Msg.prompt('SetArea', '', function(btn,text) {
                                                if(btn=='ok'){
                                                    tLcRcd.set('area',text);
                                                }
                                            });
                                        },
                                    },

                                    {text: 'Cancel',ui  : 'confirm',
                                        handler : function(){
                                            //this.disable();
                                            this.getParent().hide();
                                        },
                                    }
                                ]
                            });
                     }
                     tLcRcd=record;
                     tLcRcd.lsIndex=index;
                     var auth=record.get('author');
                     console.log("DBG LocalList auth="+auth+",userId="+userId);
                     if((auth==userId || !auth) && record.get('area')){ 
                         this.action.getAt(2).show();
                     }
                     else{
                         this.action.getAt(2).hide();
                     }

                     this.action.show();
                },
                listeners: {
                    select: function(view, record) {
                    }
                },
            },
        ],
        listeners: {
                activeitemchange: 'onActiveItemChange',
                //itemindexchange: 'onItemIndexChange'
        },

    },

    initialize:function(){
        this.callParent(arguments);
    },

    onResume:function(){
        //if(this.storeInitialed) return;
        //this.storeInitialed=true;
        var store=Ext.ComponentQuery.query("#idlocallist")[0].getStore();
        store.removeAll();
        localliststore=store;
        locallines=jlh.getShp("alllines");
        if(locallines){
            locallines=locallines.split(",");
        }
        var lines=locallines;
        for(var i=0;i<lines.length;i++){
            dbg("lines["+i+"]="+lines[i]);
            if(!lines[i]){
                locallines.splice(i,1);
                jlh.setShp("alllines",locallines.join(","));
                i--;
                continue;
            }
            var ln=jlh.getShp(lines[i]);
            if(!ln || ln=="undefined"){
                dbg(lines[i]+" no detail info exist");
                locallines.splice(i,1);
                jlh.setShp("alllines",locallines.join(","));
                i--;
                continue;
            }
            var l=JSON.parse(ln);
            var stations="";
            dbg(l.stations.length);
            for(var j=0;j< l.stations.length;j++){
                stations+=l.stations[j].stname+",";
            }
            store.add({line:lines[i],stations:stations,author:l.ownerid,area:l.area,index:i,area:l.area,jsline:l});
        }
    },

    onActiveItemChange: function(carousel, newItem, oldItem) {
        Ext.Msg.alert("LocalList onActiveItemChange:");
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
