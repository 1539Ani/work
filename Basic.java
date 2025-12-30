public class Basic {
    public static void main(String[] args) {
        var gr = new GlideRecord();
        gr.addActiveQuery();
        gr.query();

        while(gr.next()){
            gs.info('throw build error');
        }
    }
}
