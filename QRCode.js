import React, { Component, PropTypes } from 'react';
import { requireNativeComponent,DeviceEventEmitter,NativeModules} from 'react-native';
var QRcodeimage = requireNativeComponent('QRCode', QRCodeApi);

export default class QRCodeApi extends Component {
    static propTypes = {
        /**
         *
         * 定义组件需要传到原生端的属性
         * 使用React.PropTypes来进行校验
         */
            imageUrl:PropTypes.string,
            sourcesInfo:PropTypes.string,
            paymentCode:PropTypes.string,
        //iOS长按识别
            onchanges:PropTypes.func
    };
    static saveImage(amount,detail){
        return NativeModules.QRCode.save(amount,detail).then((data)=>{
            console.info('保存结果',data);

        })
    }
    _onchange=(event) =>{
        if(this.props.onChange && event.nativeEvent){
            this.props.onChange(event.nativeEvent);
        }
    }
    render() {
        return (
            <QRcodeimage {...this.props}
                onChange={this._onchange}
            />
        );
    }
}