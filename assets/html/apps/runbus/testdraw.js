Ext.application({
    name: 'Sencha',

    launch: function() {   
        var mainui=Ext.create('Ext.Container', {
            fullscreen: true,
            layout: 'vbox',
            html:"canvase<canvas id='canv0"+"' style='width:20px;height:20px'></canvas>",
        });

        var canvas = document.getElementById('canv0');
        var context = canvas.getContext("2d");
        var cw = canvas.width;
        var ch = canvas.height;

        console.log(cw,ch);
        context.lineWidth = 30;
        context.strokeStyle = 'red';
        context.moveTo(0,0);
        context.lineTo(300,150);
        context.stroke();

        context.moveTo(150,0);
        context.lineTo(150,150);
        context.stroke();

        context.moveTo(300,0);
        context.lineTo(0,150);
        context.stroke();

    }
});
