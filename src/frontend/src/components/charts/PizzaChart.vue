
<script>
import { Pie } from 'vue-chartjs';

export default {
  
  name: 'PizzaChart',

  extends: Pie,

  props: ['data', 'options'], // dados do grafico passado pelo componente que incluir esse grafico

  watch: {
    data: function (newVal) {
      // watch it
      this.renderChart(newVal, this.options);
    },
  },

  mounted() {
    this.renderChart(this.data, this.options);
  },
};
</script>
