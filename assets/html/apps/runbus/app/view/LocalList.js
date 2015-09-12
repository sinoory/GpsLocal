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
                itemTpl: '<b>{line}</b><br> {stations}',
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
                                    {
                                        text: 'Delete',
                                        ui  : 'decline'
                                    },
                                    {
                                        text: 'Download'
                                    },
                                    {
                                        text: 'Cancel',
                                        ui  : 'confirm',
                                        handler : function(){
                                            //this.disable();
                                            this.getParent().hide();
                                        },
                                    }
                                ]
                            });
                     }
                     this.action.show();
                 },
                listeners: {
                    select: function(view, record) {
                        Ext.Msg.alert('Selected!', 'You selected ' + record.get('line'));
                    }
                },
                store: {
                    fields: ['line', 'stations'],
                    //sorters: 'line',
                    data: [],
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
        var store=Ext.ComponentQuery.query("#idlocallist")[0].getStore();
        locallines=jlh.getShp("alllines");
        if(locallines){
            locallines=locallines.split(",");
        }
        var lines=locallines;
        for(var i=0;i<lines.length;i++){
            dbg("lines["+i+"]="+lines[i]);
            if(!lines[i]){
                continue;
            }
            var ln=jlh.getShp(lines[i]);
            if(!ln){
                dbg(lines[i]+" no detail info exist");
                continue;
            }
            var l=JSON.parse(ln);
            var stations="";
            dbg(l.stations.length);
            for(var j=0;j< l.stations.length;j++){
                stations+=l.stations[j].stname+",";
            }
            if(!l.ownerid){
                //linehtml+="<button id='idup"+i+"' onclick='uploadline()'>upload</button>";
            }else if(l.linechanged && l.ownerid==userId){
                //linehtml+="<button id='idup"+i+"' onclick='up2svr()'>updateServer</button>";
            }
            store.add({line:lines[i],stations:stations});
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
