#### SVG教程

##### SVG介绍

scalable vector graphics

##### SVG shapes

- Rectangle <rect>
- Circle <circle>
- Ellipse <ellipse>
- Line <line>
- Polyline <polyline>
- Polygon <polygon>
- Path <path>





<svg xmlns="http://www.w3.org/2000/svg" version="1.1"> 
    <path
       d="M150 0 L75 200 L225 200 Z"
    />
</svg>



##### SVG动画

<svg height="60" width="200">  <text x="0" y="15" fill="red" transform="rotate(30 20,40)">I love SVG</text></svg>

<svg height="128" width="128"> 
    <circle cx="64" cy="64" r="48" stroke="red" 
            stroke-width="1"/> 
    <circle cx="64" cy="64" r="36" stroke="orange"
            stroke-width="1"/>
    <circle cx="64" cy="64" r="36" stroke="yellow"
            stroke-width="1"/>
    <circle cx="64" cy="64" r="28" stroke="green"
            stroke-width="1"/>
    <circle cx="64" cy="64" r="22" stroke="cyan"
            stroke-width="1"/>
    <circle cx="64" cy="64" r="18" stroke="blue"
            stroke-width="1"/>
    <circle cx="0" cy="64" r="16" stroke="purple"
        stroke-width="1"/>
    <circle cx="0" cy="64" r="32" stroke-width="1"
            fill="white"/>



##### SVG嵌入html

SVG 文件可通过以下标签嵌入 HTML 文档：\<embed>、\<object> 或者 \<iframe>。

\<embed src="circle1.svg" type="image/svg+xml" /> 

\<object data="circle1.svg" type="image/svg+xml">\</object> 

\<iframe src="circle1.svg">\</iframe> 

