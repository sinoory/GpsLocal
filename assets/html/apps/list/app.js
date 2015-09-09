Ext.application({
    startupImage: {
        '320x460': 'resources/startup/Default.jpg', // Non-retina iPhone, iPod touch, and all Android devices
        '640x920': 'resources/startup/640x920.png', // Retina iPhone and iPod touch
    },

    isIconPrecomposed: false,
    icon: {
        57: 'resources/icons/icon.png',
        72: 'resources/icons/icon@72.png',
   },

    requires: [
        'Ext.MessageBox',
        'Ext.data.Store',
        'Ext.List',
        'Ext.plugin.PullRefresh'
    ],

    launch: function() {
        //get the configuration for the list
        var store = Ext.create('Ext.data.Store', {
            fields: ['line', 'stations'],
            sorters: 'line',
            data: [

                { line: 'Greg',    stations: 'Barry' },
            ],
        });


        var mainui=Ext.create('Ext.Container', {
            fullscreen: true,layout: 'vbox',id:'idmain',
            items: [
                {
                    xtype: 'titlebar',title:'Local Lines',
                },

                {
                    xtype: 'list',
                    flex:1,id: 'list', //must add flex:1 , other wise list will not show
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
                    store: store
                },
            ]
        });
        //var listConfiguration = this.getListConfiguration();

        //Ext.Viewport.add(listConfiguration);
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
    },

});
