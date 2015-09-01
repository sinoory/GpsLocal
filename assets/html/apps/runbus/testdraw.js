Ext.application({
    name: 'Sencha',
    requires:['Ext.draw.Component'],

    launch: function() {   
        var component = Ext.create('Ext.draw.Component',{
        items: [{
            type: 'path',
            path: 'M75,75 c0,-25 50,25 50,0 c0,-25 -50,25 -50,0',
            fillStyle: 'blue'
          }]
        });
        Ext.Viewport.setLayout('fit');
        Ext.Viewport.add(component);
    }
});
