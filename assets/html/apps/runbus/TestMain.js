Ext.define('TestMain', {
    extend: 'Ext.app.Controller',

    config: {
        refs: {
            loginForm: 'formpanel'
        },
        control: {
            'formpanel button': {
                tap: 'doLogin'
            }
        }
    },

    doLogin: function() {
        var form   = this.getLoginForm(),
            values = form.getValues();

        MyApp.authenticate(values);
    },

    launch: function() {   
    },
});
