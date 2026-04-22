document.addEventListener('DOMContentLoaded', function () {

    // Bar chart 動畫
    var bars = document.querySelectorAll('.bar');
    bars.forEach(function (bar) {
        var target = bar.style.height;
        bar.style.height = '0px';
        setTimeout(function () { bar.style.height = target; }, 300);
    });

    // Progress bar 動畫
    var progress = document.querySelector('.progress-bar');
    if (progress) {
        var w = progress.style.width;
        progress.style.width = '0%';
        setTimeout(function () { progress.style.width = w; }, 500);
    }

    // Nav active 切換
    document.querySelectorAll('.nav-item').forEach(function (item) {
        item.addEventListener('click', function (e) {
            if (this.getAttribute('href') === '#') e.preventDefault();
            document.querySelectorAll('.nav-item').forEach(function (i) {
                i.classList.remove('active');
            });
            this.classList.add('active');
        });
    });
});
