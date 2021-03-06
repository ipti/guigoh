$(document).ready(function () {
    $(".menu-icon-three").parent().addClass("active");

    $('.aside-image-title').find('a').each(function () {
        $(this).text(changeNameLength($(this).text(), 27));
    });

    $('.column-object-description').children('span').each(function () {
        $(this).text(changeNameLength($(this).text(), 100));
    });

    $('.column-object-title').find('a').each(function () {
        $(this).text(changeNameLength($(this).text(), 50));
    });
});

$(".topic-search").on('keypress', function (e) {
    if (e.keyCode == 13) {
        e.preventDefault();
    }
});

$(window).scroll(function () {
    if ($(window).scrollTop() + $(window).height() === $(document).height()) {
        $('.load-more-topics').click();
    }
});

$('span.new-topic').mousemove(function (e) {
    $('#visitorPermissionWarning').css({position: 'absolute', top: e.pageY + 10 + 'px', left: e.pageX + 'px'}).show();
});

$('span.new-topic').mouseleave(function (e) {
    $('#visitorPermissionWarning').hide();
});

jsf.ajax.addOnEvent(function (data) {
    if ($(data.source).hasClass("load-more-topics")
            || $(data.source).hasClass("topic-search")) {
        $('.column-object-description').children('span').each(function () {
            $(this).text(changeNameLength($(this).text(), 100));
        });
        $('.column-object-title').find('a').each(function () {
            $(this).text(changeNameLength($(this).text(), 50));
        });
    }
});