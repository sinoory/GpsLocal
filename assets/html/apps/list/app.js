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
        var title=Ext.create('Ext.TitleBar',{title:'BUS',id:'idbusTitle'});
        //Ext.Viewport.add(title);
        var listConfiguration = this.getListConfiguration();
        Ext.Viewport.add(listConfiguration);
        Ext.getStore();
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

    /**
     * Returns a configuration object to be used when adding the list to the viewport.
     */
    getListConfiguration: function() {
        var store = Ext.create('Ext.data.Store', {
            //give the store some fields
            fields: ['line', 'stations'],

            //filter the data using the line field
            sorters: 'line',

            //autoload the data from the server
            //autoLoad: true,

            //setup the grouping functionality to group by the first letter of the line field
/*            grouper: {
                groupFn: function(record) {
                    return record.get('line')[0];
                }
            },
*/
            data: [
            //    { line: 'Greg',    stations: 'Barry' },
            ],
        });

        return {
            //give it an xtype of list for the list component
            xtype: 'list',

            id: 'list',
/*
            scrollable: {
                indicators: false
            },
*/
            //set the itemtpl to show the fields for the store
            itemTpl: '<b>{line}</b><br> {stations}',

            //group the list
 //           grouped: true,

            //enable the indexBar
 //           indexBar: true,

            infinite: true,

            useSimpleItems: true,

            variableHeights: true,

            striped: true,

            //ui: 'round',

            //set the function when a user taps on a disclsoure icon
             onItemDisclosure: function(record, item, index, e) {
                 //show a messagebox alert which shows the persons line
                 e.stopEvent();
                 Ext.Msg.alert('Disclose', 'Disclose more info for ' + record.get('line'));
             },
            //bind the store to this list
            store: store
        };
    }
});
