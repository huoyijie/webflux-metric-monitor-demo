(function (){
    console.info('js start');
})();

var $$ = function (f) {
    f.apply();
};

function charts(canvasId, type, datasetLabel, labels, dataset, xLabelString, yLabelString) {
    var ctx = document.getElementById(canvasId).getContext('2d');
    var requestCountChart = new Chart(ctx, {
        type: type,
        data: {
            labels: labels,
            datasets: [{
                label: datasetLabel,
                data: dataset,
                backgroundColor: 'rgba(54, 162, 235, 0.2)',
                borderColor: 'rgba(54, 162, 235, 1)',
                borderWidth: 1
            }]
        },
        options: {
            scales: {
                xAxes: [{
                    scaleLabel: {
                        display: true,
                        labelString: xLabelString
                    }
                }],
                yAxes: [{
                    ticks: {
                        beginAtZero: true,
                    },
                    scaleLabel: {
                        display: true,
                        labelString: yLabelString
                    }
                }]
            }
        }
    });
}

$$(function () {
    var labels = [],
        since = '',
        reqCountDataset = [],
        qpsDataset = [],
        pct90costDataset = [];
    $.get('/api/statics/hour', {}, function(resp) {
        since = resp[0].since;
        for(var i = 0; i < resp.length; i++) {
            labels.push(i);
            reqCountDataset.push(resp[i].totalCount);
            qpsDataset.push(resp[i].qps);
            pct90costDataset.push(resp[i].pct90Cost);
        }
        charts("requestCountChart", 'bar', 'request count',
            labels, reqCountDataset,
            'minutes (since ' + since + ')', 'reqs/min');
        charts("qpsChart", 'line', 'qps',
            labels, qpsDataset,
            'minutes (since ' + since + ')', 'qps');
        charts("90costChart", 'line', '90 cost',
            labels, pct90costDataset,
            'minutes (since ' + since + ')', '90 cost (ms)');
    });
});
