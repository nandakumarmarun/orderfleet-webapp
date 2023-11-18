if (!this.BatteryPercentage) {
    this.BatteryPercentage = {};
}
(function() {
    'use strict';
    var mobileBatteryPercentageContextPath = location.protocol + '//'
        + location.host + location.pathname;
    // var mobileBatteryPercentageModel = {
    //     employeeProfilePid : null
    // };
    $(document).ready(function() {
         BatteryPercentage.filter ();
    });
    BatteryPercentage.filter = function() {

        $('#tbodyemployeeMobileBattery').html(
            "<tr><td colspan='3' align='center'>Please wait...</td></tr>");
        $
            .ajax({
                url : mobileBatteryPercentageContextPath + "/filter",
                type : 'GET',
                data : {
                    employeePid : $('#dbEmployee').val()
                },
                success : function(employeeProfiles) {

                    if (employeeProfiles.length == 0) {
                        $('#tbodyemployeeMobileBattery')
                            .html(
                                "<tr><td colspan='2' align='center'>No data available</td></tr>");
                        return;
                    } else {
                        $('#tbodyemployeeMobileBattery').html("");
                        $
                            .each(
                                employeeProfiles,
                                function(index, employeeProfile) {

                                    $(
                                        '#tbodyemployeeMobileBattery')
                                        .append(
                                            "<tr><td>"
                                            + employeeProfile.name
                                            + "</td><td>"
                                            +employeeProfile.userLogin
                                            +"</td><td>"
                                            +employeeProfile.batteryPercentage
                                            +"</td></tr>");

                                });

                    }
                }

            });
    }

})();