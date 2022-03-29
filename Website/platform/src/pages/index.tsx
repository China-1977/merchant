import React from 'react';
import mapboxgl from 'mapbox-gl';


/**
 * version：样式版本，当前必须必须设置8。
 name：样式名称，设置一个可读的名称描述即可。
 sprite：mapbox地图使用的图标。
 glyphs：mapbox地图使用的标注字体。
 sources： mapbox地图使用的地图服务资源定义。
 layers： mapbox地图使用的图层定义。
 *
 * @constructor
 */
export default function IndexPage() {
  React.useEffect(() => {
    var map = new mapboxgl.Map({
      container: 'map',
      style: {
        version: 8,
        name: '1977',
        sprite: 'localhost:8010/sprite',
        sources: {
          'raster-tiles': {
            type: 'raster',
            tiles: [
              'http://127.0.0.1:8010/{z}/{x}/{y}.png'
            ],
          }
        },
        'layers': [{
          'id': 'simple-tiles',
          'type': 'raster',
          'source': 'raster-tiles',
          'minzoom': 0,
          'maxzoom': 18
        }]
      },
      center: [116.85059, 36.78289],
      zoom: 5
    });
  });
  return (
    <div id="map" style={{width: "100%", height: "100vh"}} className="marker"/>
  );
}
