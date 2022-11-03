(function (doc, win) {

  let docEl = doc.documentElement;
  let resizeEvt = 'orientationchange' in window ? 'orientationchange' : 'resize';

  // 当视口大小改变时 更改根元素字体大小
  let recalc = () => {

    let clientWidth = docEl.clientWidth; // contentWidth + padding
    if (!clientWidth) return;
    docEl.style.fontSize = 16 * (clientWidth / 320) + 'px';
  };

  // Support: IE 11, Edge
  if (doc.addEventListener) {

    win.addEventListener(resizeEvt, recalc, false);
    doc.addEventListener('DOMContentLoaded', recalc, false);
  }
  // Support: IE 9 - 10 only
  if (doc.attachEvent) {

    win.attachEvent('on' + resizeEvt, recalc);
    doc.attachEvent('onDOMContentLoaded', recalc);
  }
})(document, window);
