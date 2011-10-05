jQuery(function(){
  // group gallery items
  jQuery('div.fancy-gallery a')
  .each(function(){
    var $this = jQuery(this);
    $this.attr('rel', $this.parent().attr('id'));
  });
  // Add Fancy Classes to single items:
  jQuery('a').each(function(){
    // filter items
    if (          this.href.substr(-4).toLowerCase().indexOf('.jpg') < 0 &&
                  this.href.substr(-5).toLowerCase().indexOf('.jpeg') < 0 &&
                  this.href.substr(-4).toLowerCase().indexOf('.png') < 0 &&
                  this.href.substr(-4).toLowerCase().indexOf('.gif') < 0 &&
                  this.href.substr(-4).toLowerCase().indexOf('.bmp') < 0 &&
                  this.href.substr(-5).toLowerCase().indexOf('.wbmp') < 0 &&
                  this.href.substr(-4).toLowerCase().indexOf('.ico') < 0 &&
                  true )
    return;
    // shorter access path
    var $lnk = jQuery(this);
    var $img = $lnk.find('img');
    // Add the fancybox class
    $lnk.addClass('fancybox');
    // Copy the title tag from link to img
    $lnk.attr('title', $img.attr('title'));
  });
  jQuery('a.fancybox')
  .unbind('click')
  .fancybox({
    padding        :  10,
    cyclic         :  false,
    scrolling      : 'auto',
    centerOnScroll :  false,
    overlayOpacity :  0.3,
    overlayColor   : '#666',
    titleShow      :  true,
    titlePosition  : 'float',
    transitionIn   : 'fade',
    transitionOut  : 'fade',
    speedIn        :  300,
    speedOut       :  300,
    changeSpeed    :  300,
    showCloseButton:  true
  });
});
