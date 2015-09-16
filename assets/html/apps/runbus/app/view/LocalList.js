Ext.define('RunBus.view.LocalList',{
    xtype:'tpLocalList',
    extend:'Ext.Container',
    config: {
        fullscreen: true,
        layout:'vbox', //TODO:impotant , otherwise the list can'b show
        items: [
            {
                xtype: 'titlebar',title:'Local Lines',
                items:[
                    {xtype:'button',html:'<',ui:'action',align:'right',
                        handler:function(){Ext.Viewport.setActiveItem(0);}
                    },
                    {xtype:'button',html:'>',ui:'action',align:'right',
                        handler:function(){Ext.Viewport.setActiveItem(2);}
                    },
                ],

            },
            {
                xtype: 'list',
                flex:1,id: 'idlocallist', //must add flex:1 , other wise list will not show
                itemTpl: '<b>{author} {area} {line}</b><br> {stations}',
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
                                            localliststore.remove(tmpLocalListRecord);
                                            jlh.rmShp(tmpLocalListRecord.get('line'));
                                            locallines.splice(tmpLocalListRecord.get('index'),1);
                                            jlh.setShp("alllines",locallines.join(","));
                                            jlh.setShp("lastLine","");
                                            busControl.getBusStations();
                                            this.getParent().hide();
                                        },
                                    },
                                    {text: 'Select',
                                        handler : function(){
                                            jlh.setShp("lastLine",tmpLocalListRecord.get('line'));
                                            busControl.getBusStations();
                                            this.getParent().hide();
                                        },
                                    },
                                    {text: 'Upload',
                                        handler : function(){
                                            if(tmpLocalListRecord.get('author')){
                                            }
                                            this.getParent().hide();
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
                     tmpLocalListRecord=record;
                     var auth=record.get('author');
                     console.log("DBG LocalList auth="+auth+",userId="+userId);
                     if((auth==userId || !auth) && record.get('area')){ this.action.getAt(2).show();}
                     else this.action.getAt(2).hide();

                     this.action.show();
                },
                listeners: {
                    select: function(view, record) {
                    }
                },
                store: {
                    fields: ['line', 'stations','author','shortarea','index'],
                    //sorters: 'line',
                    data: [
                    //{'line':'line','stations':'aa,bb,cc','author':'author'},
                        ],
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
            //dbg("lines["+i+"]="+lines[i]);
            if(!lines[i]){
                continue;
            }
            var ln=jlh.getShp(lines[i]);
            if(!ln){
                //dbg(lines[i]+" no detail info exist");
                continue;
            }
            var l=JSON.parse(ln);
            var stations="";
            //dbg(l.stations.length);
            for(var j=0;j< l.stations.length;j++){
                stations+=l.stations[j].stname+",";
            }
            var shortarea=l.area;
            if(shortarea){
                if(shortarea.indexOf("区")!=-1){
                    shortarea=shortarea.substring(0,shortarea.indexOf("区"));
                }
            }
            store.add({line:lines[i],stations:stations,author:l.ownerid,area:l.area,index:i,shortarea:shortarea});
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
