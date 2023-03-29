
const colors: { [key: string]: string } = { colorDark: '#112233', colorMedium: '#223344', colorLight: '#334455', colorTransparent: '#00000000' }
function css(template: TemplateStringsArray): string {

    let str = template.raw.join('')
    Object.keys(colors).forEach(i => {
        str = str.split(i).join(colors[i])
    });
    return str;
}

export const style: string = css`
    #main {
        background-color: colorDark;
        border: none;
    }

    #page {
        background-color: colorMedium;
        border: 1px solid colorDark;
    }

    #scrollarea {
        background-color: colorDark;
        border: 1px solid colorDark;
    }

    #button {
        color: #AABBCC;
        background-color: colorLight;
    }

    #textbox {
        color: #AABBCC;
        background-color: colorLight;
        font: 14px;
        border: 1px solid colorDark;
    }

    #title {
        color: #BBCCDD;
        font: bold 22px;
    }

    #label {
        color: #BBCCDD;
        font: bold 14px;
    }

    #box {
        background-color: colorTransparent;
        border: none;
    }

    QScrollBar:vertical {
        background: colorDark;
        width: 8px;
        margin: 0;
        border-radius: 0px;
    }
  
    QScrollBar::handle:vertical {	
        background-color: colorMedium;
        border-radius: 7px;
    }
    QScrollBar::handle:vertical:hover{	
        background-color: colorMedium;
    }
    QScrollBar::handle:vertical:pressed {	
        background-color: colorLight;
    }

    QScrollBar::sub-line:vertical, QScrollBar::sub-line:vertical:hover, QScrollBar::sub-line:vertical:pressed,
    QScrollBar::add-line:vertical, QScrollBar::add-line:vertical:hover, QScrollBar::add-line:vertical:pressed,
    QScrollBar::up-arrow:vertical, QScrollBar::down-arrow:vertical, QScrollBar::add-page:vertical, QScrollBar::sub-page:vertical {
        background: none;
    }

`

// console.log(style)
